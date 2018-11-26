//https://discourse.processing.org/t/processing-to-draw-naca-4-digit-airfoils/5739


int N = 81;  //number of points
float M = -0.00000f; //2/100.0;
float[] dydx = new float[81];
float T = 0/100.0;
float dT;

float x[] = new float[81];  //divide up unit chord length by N
float y[] = new float[81];  //divide up unit chord length by N
float xU[] = new float[81];
float yU[] = new float[81];
float xL[] = new float[81];
float yL[] = new float[81];
float P = 0.4; //40/100.0;
PShape s,s2,s3,s4,s5;
float a0 = 0.2969;
float a1 = -0.126;
float a2 = -0.3516;
float a3 = 0.2843;
float a4 = -0.1036;
float[] theta = new float[81];
float[] dummy = new float[81];
float[] dummy2 = new float[81];
float[] yt = new float[81];
String[] coordinates = new String[81];

float[] beta = new float[81];

Table table;
TableRow row;

String nameOfFile = "Airfoil";
float Dat[]= new float[81];

Controls controls;
Controls controls2;
HorizontalControl controlX;
int showControls;
boolean draggingZoomSlider = false;
boolean released = true;
float zoom = 0.00f;
float tzoom = 0.00f; //183.56482f; //120.37037f and 12/100 thickness;
float velocityX = 0;
float velocityY = 0;
float angle;

void setup()
{
//  fullScreen(P3D);
 size( 650, 300, P3D);  
 
 controls = new Controls();
 controlX = new HorizontalControl();
 showControls = 1;     
 table = new Table();
 makeCSVFileRows("",Dat);
 WriteDataToCSVFile(Dat, "");
 WriteDataToCSVFile(Dat, "");
 saveTable(table, "data/"+nameOfFile+".csv");
 getOrdinates();
 airfoiltest(xU,yU);
 airfoiltest(xL,yL);
 dummy[0] = 1.00000f;
 dummy[1] = 1.00000f;
 dummy2[0] = 0.00000f;
 dummy2[1] = 0.00000f;
 println ("done.");
 } 

void draw() {

    if (mousePressed) {
     if( (showControls == 1) && (controls.isZoomSliderEvent(mouseX, mouseY)) || ( showControls == 1 && controlX.isZoomSliderEvent(mouseX,mouseY))) {
        draggingZoomSlider = true;
       
       
   zoom = controls.getZoomValue(mouseY);


   tzoom = controlX.getZoomValue(mouseX,mouseY);      
  println(zoom,tzoom);
             load_file();
       // M = map(M,-0.10,0.10,-264.00,264.00); //,-0.10,0.10);
         M = map(M,-0.10,0.10,-zoom,zoom); //,-0.10,0.10);
        T = map(T,0,12,-tzoom,tzoom);
    //  P = map(P,0.2, 0.8,-tzoom,tzoom);
     }    
           // MousePress - Rotation Adjustment
  else if (!draggingZoomSlider) {
        if (released == true){
         velocityX += (mouseY-pmouseY) * 0.01;
         velocityY -= (mouseX-pmouseX) * 0.01;
       
        }      
     }
  }
  
   background(0, 0, 0);
   
   
  if (showControls == 1) {
     controls.render(); 
         controlX.render(); 
  }

  controls.updateZoomSlider(zoom);
  controlX.updateZoomSlider(tzoom);
  


 translate(width/2-450/2, height/2);

 scale(1.5,1);
//makeCSVFile("Airfoil");

makeCSVFileRows(nameOfFile,xU);
// WriteDataToCSVFile(xU,"");
// WriteDataToCSVFile(yU,""); 

  dT = sqrt((x[80]-x[0])*(x[80]-x[0]) + (y[80]-y[0])*(y[80]-y[0]));
  dT = (asin(y[80]/dT));

  thread( "getOrdinates");

 // rotate( radians(8) ); //12/100 thickness
 // rotate( radians(14) );  //.19560185 thick
 //////////////////   rotate( radians(4) );  // .063194446 thick
rotate((angle));

  shape(airfoiltest(xU,yU), 0, 0);

  //rotate(radians(-dT));
//  rotate(radians(-17));  //12/100 thickness
 //   rotate(radians(-27)); //.19560185 thick
 /////////////////////       rotate(radians(-9)); //.063194446 thick
rotate((-2*(angle)));


  shape(airfoiltest(xL,yL), 0, 0);

//noLoop();

}


PShape airfoiltest(float[] xvalue, float[] yvalue) {
  
   s = createShape(); 
   s.beginShape();
// s.fill(125);
   s.noFill();
   s.stroke(0,255,0);
   s.strokeWeight(3);
 
   for (int i = 0; i<coordinates.length-1; i++){
    s.vertex(300*xvalue[i],-300* (yvalue[i]));

 
}
 // println(yvalue[80]);
   s.endShape(CLOSE);
   return s;
}

void load_file() {
  
coordinates = loadStrings("Airfoil.csv");  // also could use loadtable
//println("Loaded " + coordinates.length + " coordinates:");
//   println( ": ", String.format("%.5f %.5f", "", ""));
 for (int i = 0; i < coordinates.length; i++) {
   String[] ordinate = splitTokens(coordinates[i], ",");
   if(i >81 && i <82){
   x[i] = float(ordinate[i]);
   y[i] = float(ordinate[i]);
   println(i, ": ", String.format("%.5f %.5f", x[i], y[i]));
   }
  //  println(i, ": ", String.format("%.5f %.5f", x[i], y[i]));
 }
}

void WriteDataToCSVFile(float array[], String nameOfData) {
  
 table.addColumn(nameOfData, Table.FLOAT);
 for (int i = 0; i< array.length; i++) {
   table.setFloat(i, nameOfData, array[i]);
 }//for
}//func

void makeCSVFileRows(String name, float array[]) {      
 for (int i = 0; i< array.length; i++) {
   row = table.addRow();
 }
}

void getOrdinates(){
   
for (int i = 0; i < N; i++){
  
  M = zoom/1000f;
  T = tzoom/1000f;
  angle = map(T,0.00f,12.000f,0.00f,14.50f);
  
//  coordinates = loadStrings(csvfile);  // also could use loadtable 
//      String[] ordinate = splitTokens(coordinates[i], " ");
  if(i >= 0){
       beta[i] = (radians(180)/81.0) * i;
       x[i] = (1 - cos(beta[i]))/2;
     }
 if(i < 40){
     if (x[i] >= 0){
     y[i] = (M/(P*P) * (2*P*x[i] - x[i]*x[i]));
     dydx[i] = (2*M)/(P*P) * (P - x[i]);
   }    
 }  
 if(i >=40){  
    if (x[i] <= 1.00000f){     
    y[i] = (M/(1-P)*(1-P) * (1 - 2*P + 2*P*x[i] - x[i]*x[i]))* 2.75; //*2.686; 
    dydx[i] = (2*M/((1-P)*(1-P)) * (P - x[i])); 
   } 
 }    
}  //end for loop

for (int i = 0; i < 81; i++){
 if (i >= 0 && i < 81) {   
    yt[i] = (T/0.2* (sqrt(a0*x[i])+ a1*x[i] + a2*(x[i]*x[i]) + a3*(x[i]*x[i]*x[i]) + a4*(x[i]*x[i]*x[i]*x[i])));
    theta[i] = (atan((dydx[i])));
    xU[i] = x[i] - yt[i] * -(sin(radians(theta[i]))); 
//  WriteDataToCSVFile(xU,"");
    yU[i] = ((y[i] + yt[i]  * (cos(radians(theta[i]))))); //*atan((y[i]-yt[i])/x[i]);
//  WriteDataToCSVFile(yU,""); 
  xL[i] = x[i] + yt[i] * sin(radians(theta[i]));        
//  WriteDataToCSVFile(xL,"");
  yL[i] = (y[i] - yt[i] * cos(radians(theta[i]))); //*asin((yt[i]*slope[i])); 
//  WriteDataToCSVFile(yL,"");
//  println(xU[i],yU[i],M,T);
//dummy[i] = yU[80];
//dummy2[i] = yL[80];
   }
}


}

void mouseReleased() {

if (released == true  && draggingZoomSlider == true){
    
}

draggingZoomSlider = false;
}
