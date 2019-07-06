// https://discourse.processing.org/t/processing-to-draw-naca-4-digit-airfoils/5739
// http://www.airfoiltools.com/airfoil/naca4digit?MNaca4DigitForm%5Bcamber%5D=05&MNaca4DigitForm%5Bposition%5D=40&MNaca4DigitForm%5Bthick%5D=30&MNaca4DigitForm%5BnumPoints%5D=81&MNaca4DigitForm%5BcosSpace%5D=0&MNaca4DigitForm%5BcosSpace%5D=1&MNaca4DigitForm%5BcloseTe%5D=0&MNaca4DigitForm%5BcloseTe%5D=1&yt0=Plot
// http://www.pdas.com/refs/tm4741.pdf   //different calc
// https://discourse.processing.org/t/trying-to-use-modelx-but-built-a-spiral-galaxy-of-points/6064
// https://discourse.processing.org/t/trying-to-use-modelx-but-built-a-spiral-galaxy-of-points/6064/12

int N = 81;  //number of points
float M = -0.00000f; //2/100.0;
float[] dydx = new float[81];
float T = 0/100.0;
float[] dT;
float x[] = new float[81];  //divide up unit chord length by N
float y[] = new float[81];  //divide up unit chord length by N
float xU[] = new float[81];
float yU[] = new float[81];
float xL[] = new float[81];
float yL[] = new float[81];
float P = 0.4; //0.4; //40/100.0;
PShape s,s2;
PFont font;
float a0 = 0.2969;
float a1 = -0.126;
float a2 = -0.3516;
float a3 = 0.2843;
float a4 = -0.1036;
float[] theta = new float[81];
float[] yt = new float[81];
float[] beta = new float[81];
Controls controls;
Controls controls2;
HorizontalControl controlX;
int showControls;
boolean draggingZoomSlider = false;
boolean released = true;
float zoom = 0.00f;
float tzoom = 0.00f; //183.56482f; //120.37037f and 12/100 thickness;
float angle;
float[] Xvalues = new float[162];
float[] Yvalues = new float[162];
float[] XUoord = new float[81];
float[] YUoord = new float[81];
float[] XLoord = new float[81];
float[] YLoord = new float[81];

// now the program starts without the file, even without the /data dir what a write could create automatic
String outfilename  = "data/new.csv";   // take out of draw and make new key [s] for save to file


//_____________________________________________________________
void save_to_file() {
  Table table = new Table(); 
  table.addColumn("X");
  table.addColumn("Y");

  for (int i = 159; i > N-1; i--) {
    TableRow newRow = table.addRow();
    newRow.setFloat("X", Xvalues[i] );
    newRow.setFloat("Y", Yvalues[i] );
  }  
  for (int i = 0; i < N-1; i++) {
    TableRow newRow = table.addRow();
    newRow.setFloat("X", Xvalues[i] );
    newRow.setFloat("Y", Yvalues[i] );
  }
  saveTable(table, outfilename);
  println("data saved to "+outfilename);
}

void keyPressed() {
  if ( key == 's' )  save_to_file();
}

void mouseWheel(MouseEvent event) {
  float e = event.getCount();
 // if ( keyPressed )  P += 0.01*e; //&& key == 'p' )  P += 0.01*e; 
  P += 0.01*e;
}

void setup()
{
 size( 650, 300, P3D); 
   font = loadFont("CourierNew36.vlw"); 
 
 controls = new Controls();
 controlX = new HorizontalControl();
 showControls = 1;     
 getOrdinates();
 println ("done.");
}

 void draw() {
   

    if (mousePressed) {
     if( (showControls == 1) && (controls.isZoomSliderEvent(mouseX, mouseY)) || ( showControls == 1 && controlX.isZoomSliderEvent(mouseX,mouseY))) {
         draggingZoomSlider = true;     
         zoom = controls.getZoomValue(mouseY);
         tzoom = controlX.getZoomValue(mouseX,mouseY);      
       //  M = map(M,-0.10,0.10,-zoom,zoom); //,-0.10,0.10);
      //   T = map(T,0,12,-tzoom,tzoom);
     }    
           // MousePress - Rotation Adjustment
      else if (!draggingZoomSlider) {
         if (released == true) {
             M = map(M,-0.10,0.10,-zoom,zoom); //,-0.10,0.10);
         T = map(T,0,12,-tzoom,tzoom);
            }      
     }
  }
  background(0, 0, 0);
  stroke(182,185,188);
  strokeWeight(1);
  line( width-width, height/2,width, height/2); 
     textFont(font, 15);
  textAlign(LEFT, BOTTOM);
// stroke(0x00ff00);
  text("Airfoil:\nCamber  : "+M + "\nThickness: "  +T + "\nHighpoint: "  + P  , width/2+150, height/2+120);

    if (showControls == 1) {
     controls.render(); 
         controlX.render(); 
    }
  controls.updateZoomSlider(zoom);
  controlX.updateZoomSlider(tzoom);

  translate(width/2-450/2, height/2);
  scale(1.5,-1.5);
  thread( "getOrdinates");
  stroke(0,255,0);
  strokeWeight(4);
  Xvalues = concat(xU,xL);
  Yvalues = concat(yU,yL);
  XLoord = subset(Xvalues,N,(N-1));
  YLoord = subset(Yvalues,N,(N-1));
   s = createShape(); 
   s.beginShape();
// s.fill(125);
   s.noFill();
   s.stroke(0,255,0);
   s.strokeWeight(2);

        for (int i = 0; i<(N-1); i++){ 
           s.vertex(300*XLoord[i],300*YLoord[i]);
       
           angle = sqrt( ((300 - Xvalues[0]) * (300 - Xvalues[0])) + ((300*Yvalues[N-1]) * (300*Yvalues[N-1])) );
           angle = asin(300*Yvalues[N-1]/angle);
        }
        XLoord = reverse(XLoord);
        YLoord = reverse(YLoord);
        for (int i = 0; i<(N-1); i++){ 
           s.vertex(300*XLoord[i],300*YLoord[i]);
       
           angle = sqrt( ((300 - Xvalues[0]) * (300 - Xvalues[0])) + ((300*Yvalues[N-1]) * (300*Yvalues[N-1])) );
           angle = asin(300*Yvalues[N-1]/angle);
        }
               
   s.endShape(CLOSE);
   shearY(angle); 
   shape(s,0,0);
   shearY(-angle);

   XUoord = subset(Xvalues,0,(N-1));
   YUoord = subset(Yvalues,0,(N-1));
   s2 = createShape(); 
   s2.beginShape();
// s.fill(125);
   s2.noFill();
   s2.stroke(0,255,0);
   s2.strokeWeight(2);


        for (int i = 0; i<(N-1); i++){ 
           s2.vertex(300*XUoord[i],300*YUoord[i]);
       
           angle = sqrt( ((300 - Xvalues[0]) * (300 - Xvalues[0])) + ((300*Yvalues[N-1]) * (300*Yvalues[N-1])) );
           angle = asin(300*Yvalues[N-1]/angle);
        } 
        XUoord = reverse(XUoord);
        YUoord = reverse(YUoord);
               for (int i = 0; i<(N-1); i++){ 
           s2.vertex(300*XUoord[i],300*YUoord[i]);
        
           angle = sqrt( ((300 - Xvalues[0]) * (300 - Xvalues[0])) + ((300*Yvalues[N-1]) * (300*Yvalues[N-1])) );
           angle = asin(300*Yvalues[N-1]/angle);
        }
          
   s2.endShape(CLOSE);
   shearY(-angle); 
   shape(s2,0,0);

  //    Xvalues = concat(xU, reverse(xL));               // return: set the globals with the results
//  Yvalues = concat(yU,reverse(yL));
        
}


void getOrdinates(){
   
  for (int i = 0; i < N; i++){
  
    M = zoom/2500f;
    T = tzoom/2500f;

    if(x[i] >= 0){
       beta[i] = (radians(180)/81.0) * i;
       x[i] = (1 - cos(beta[i]))/2;
     }
    if(x[i] < P){
     if (x[i] >= 0){
     y[i] = ((M/(P*P) * (2*P*x[i] - x[i]*x[i])));  // /1.005;
     dydx[i] = (2*M)/(P*P) * (P - x[i]);
     }    
   }  
    if(x[i] >=P){  
    if (x[i] <= 1.00000f){     
    y[i] = (M/((1-P)*(1-P)) * (1 - 2*P + 2*P*x[i] - x[i]*x[i])); //*2.725; //* 2.75; //*2.686; 
    dydx[i] = (2*M/((1-P)*(1-P)) * (P - x[i])); 
     } 
   }    
}  //end for loop

   for (int i = 0; i < 81; i++){
   
       yt[i] = (T/0.2* (a0*sqrt(x[i])+ a1*x[i] + a2*(x[i]*x[i]) + a3*(x[i]*x[i]*x[i]) + a4*(x[i]*x[i]*x[i]*x[i])));
       theta[i] = (atan((dydx[i])));
       xU[i] = x[i] - yt[i] * (sin(radians(theta[i]))); 
       yU[i] = ( (y[i] + yt[i]  * (cos(radians(theta[i]))) ) );
       xL[i] = x[i] + yt[i] * sin(radians(theta[i]));        
       yL[i] = (y[i] - yt[i] * cos(radians(theta[i]))); 
         
    }
    


}
