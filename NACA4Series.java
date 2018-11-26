import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class NACA4Series extends PApplet {

//https://discourse.processing.org/t/processing-to-draw-naca-4-digit-airfoils/5739


int N = 81;  //number of points
float M = -0.00000f; //2/100.0;
float[] dydx = new float[81];
float T = 0/100.0f;
float dT;

float x[] = new float[81];  //divide up unit chord length by N
float y[] = new float[81];  //divide up unit chord length by N
float xU[] = new float[81];
float yU[] = new float[81];
float xL[] = new float[81];
float yL[] = new float[81];
float P = 0.4f; //40/100.0;
PShape s,s2,s3,s4,s5;
float a0 = 0.2969f;
float a1 = -0.126f;
float a2 = -0.3516f;
float a3 = 0.2843f;
float a4 = -0.1036f;
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

public void setup()
{
//  fullScreen(P3D);
   
 
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

public void draw() {

    if (mousePressed) {
     if( (showControls == 1) && (controls.isZoomSliderEvent(mouseX, mouseY)) || ( showControls == 1 && controlX.isZoomSliderEvent(mouseX,mouseY))) {
        draggingZoomSlider = true;
       
       
   zoom = controls.getZoomValue(mouseY);


   tzoom = controlX.getZoomValue(mouseX,mouseY);      
  println(zoom,tzoom);
             load_file();
       // M = map(M,-0.10,0.10,-264.00,264.00); //,-0.10,0.10);
         M = map(M,-0.10f,0.10f,-zoom,zoom); //,-0.10,0.10);
        T = map(T,0,12,-tzoom,tzoom);
    //  P = map(P,0.2, 0.8,-tzoom,tzoom);
     }    
           // MousePress - Rotation Adjustment
  else if (!draggingZoomSlider) {
        if (released == true){
         velocityX += (mouseY-pmouseY) * 0.01f;
         velocityY -= (mouseX-pmouseX) * 0.01f;
       
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

 scale(1.5f,1);
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


public PShape airfoiltest(float[] xvalue, float[] yvalue) {
  
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

public void load_file() {
  
coordinates = loadStrings("Airfoil.csv");  // also could use loadtable
//println("Loaded " + coordinates.length + " coordinates:");
//   println( ": ", String.format("%.5f %.5f", "", ""));
 for (int i = 0; i < coordinates.length; i++) {
   String[] ordinate = splitTokens(coordinates[i], ",");
   if(i >81 && i <82){
   x[i] = PApplet.parseFloat(ordinate[i]);
   y[i] = PApplet.parseFloat(ordinate[i]);
   println(i, ": ", String.format("%.5f %.5f", x[i], y[i]));
   }
  //  println(i, ": ", String.format("%.5f %.5f", x[i], y[i]));
 }
}

public void WriteDataToCSVFile(float array[], String nameOfData) {
  
 table.addColumn(nameOfData, Table.FLOAT);
 for (int i = 0; i< array.length; i++) {
   table.setFloat(i, nameOfData, array[i]);
 }//for
}//func

public void makeCSVFileRows(String name, float array[]) {      
 for (int i = 0; i< array.length; i++) {
   row = table.addRow();
 }
}

public void getOrdinates(){
   
for (int i = 0; i < N; i++){
  
  M = zoom/1000f;
  T = tzoom/1000f;
  angle = map(T,0.00f,12.000f,0.00f,14.50f);
  
//  coordinates = loadStrings(csvfile);  // also could use loadtable 
//      String[] ordinate = splitTokens(coordinates[i], " ");
  if(i >= 0){
       beta[i] = (radians(180)/81.0f) * i;
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
    y[i] = (M/(1-P)*(1-P) * (1 - 2*P + 2*P*x[i] - x[i]*x[i]))* 2.75f; //*2.686; 
    dydx[i] = (2*M/((1-P)*(1-P)) * (P - x[i])); 
   } 
 }    
}  //end for loop

for (int i = 0; i < 81; i++){
 if (i >= 0 && i < 81) {   
    yt[i] = (T/0.2f* (sqrt(a0*x[i])+ a1*x[i] + a2*(x[i]*x[i]) + a3*(x[i]*x[i]*x[i]) + a4*(x[i]*x[i]*x[i]*x[i])));
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

public void mouseReleased() {

if (released == true  && draggingZoomSlider == true){
    
}

draggingZoomSlider = false;
}
/*

 Kepler Visualization - Controls
 
 GUI controls added by Lon Riesberg, Laboratory for Atmospheric and Space Physics
 lon@ieee.org
 
 April, 2012
 
 Current release consists of a vertical slider for zoom control.  The slider can be toggled
 on/off by pressing the 'c' key.
 
 Slide out controls that map to the other key bindings is currently being implemented and
 will be released soon.
 
*/

class Controls {
   
   int barWidth;   
   int barX;                          // x-coordinate of zoom control
   int minY, maxY;                    // y-coordinate range of zoom control
   float minZoomValue, maxZoomValue;  // values that map onto zoom control
   float valuePerY;                   // zoom value of each y-pixel 
   int sliderY;                       // y-coordinate of current slider position
   float sliderValue;                 // value that corresponds to y-coordinate of slider
   int sliderWidth, sliderHeight;
   int sliderX;  // x-coordinate of left-side slider edge

                       
   
   Controls () {
      
      barX = 40;
      barWidth = 15;
 
      minY = 40;
      maxY = minY + height/3 - sliderHeight/2;
           
      minZoomValue = height - height;
      maxZoomValue = height;   // 300 percent
      valuePerY = (maxZoomValue - minZoomValue) / (maxY - minY);
      
      sliderWidth = 25;
      sliderHeight = 10;
      sliderX = ((barX + (barWidth/2)) - (sliderWidth/2));      
      sliderValue = minZoomValue; 
      sliderY = minY;     
   }
   
   
   public void render() {

     // strokeWeight(1.5); 
        strokeWeight(1); 
    //  stroke(105, 105, 105);  // fill(0xff33ff99);
   //   stroke(0xff33ff99);  // fill(0xff33ff99);  0xffff0000
       stroke(0xffff0000);
      
      // zoom control bar
      fill(0, 0, 0, 0);
        
      rect(barX, minY, barWidth, maxY-minY);
      
      // slider
     // fill(105, 105, 105); //0x3300FF00
       fill(0xffff0000); // 0xff33ff99//0x3300FF00
      rect(sliderX, sliderY, sliderWidth, sliderHeight);
   }
   
   
   public float getZoomValue(int y) {
      if ((y >= minY) && (y <= (maxY - sliderHeight/2))) {
         sliderY = (int) (y - (sliderHeight/2));     
         if (sliderY < minY) { 
            sliderY = minY; 
         } 
         sliderValue = (y - minY) * valuePerY + minZoomValue;
      }     
      return sliderValue;
   }
   
   
   public void updateZoomSlider(float value) {
      int tempY = (int) (value / valuePerY) + minY;
      if ((tempY >= minY) && (tempY <= (maxY-sliderHeight))) {
         sliderValue = value;
         sliderY = tempY;
      }
   }
   
   
   public boolean isZoomSliderEvent(int x, int y) {
      int slop = 50;  // number of pixels above or below slider that's acceptable.  provided for ease of use.
      int sliderTop = (int) (sliderY - (sliderHeight/2)) - slop;
      int sliderBottom = sliderY + sliderHeight + slop;
      return ((x >= sliderX) && (x <= (sliderX    + sliderWidth)) && (y >= sliderTop)  && (y <= sliderBottom) || draggingZoomSlider );
   } 
}
 
/*
I modified this so the slider is horizontal.  That gives me a vertical for
tweaking altitude and horizontal for right ascension/longitude
*/

/*

 Kepler Visualization - Controls
 
 GUI controls added by Lon Riesberg, Laboratory for Atmospheric and Space Physics
 lon@ieee.org
 
 April, 2012
 
 Current release consists of a vertical slider for zoom control.  The slider can be toggled
 on/off by pressing the 'c' key.
 
 Slide out controls that map to the other key bindings is currently being implemented and
 will be released soon.
 
*/

class HorizontalControl {
   
   int barHeight;   
   int barY;                          // y-coordinate of zoom control
   int minX, maxX;                    // x-coordinate range of zoom control
   float minZoomValue, maxZoomValue;  // values that map onto zoom control
   float valuePerX;                   // zoom value of each y-pixel 
   int sliderY;                       // y-coordinate of current slider position
   float sliderValue;                 // value that corresponds to y-coordinate of slider
   int sliderWidth, sliderHeight;
   int sliderX;                       // x-coordinate of left-side slider edge                     
   
   HorizontalControl () {
      
      barY = 15; //40;
      barHeight = 40; //15;
 
      minX = 40;
      maxX = minX + width/3 - sliderWidth/2;
           
      minZoomValue = width - width;
      maxZoomValue = width;   // 300 percent
      valuePerX = (maxZoomValue - minZoomValue) / (maxX - minX);
      
      sliderWidth = 10; //25;
      sliderHeight = 25; //10;
     // sliderY = (barY + (barHeight/2)) - (sliderHeight/2);
      sliderY = (barY - (sliderHeight/2)) + (barHeight/2);
      sliderValue = minZoomValue; 
      sliderX = minX;     
   }
   
   
   public void render() {
       pushMatrix();


     // strokeWeight(1.5); 
        strokeWeight(1); 
    //  stroke(105, 105, 105);  // fill(0xff33ff99);
   //   stroke(0xff33ff99);  // fill(0xff33ff99);  0xffff0000
       stroke(0xffff0000);
      
      // zoom control bar
      fill(0, 0, 0, 0);
        
      rect(minX,barHeight + height - height/4,maxX-minX, barY );
     // rect(maxX-minX, barHeight/2,minX,barY + height - height/4 );
      
      // slider
     // fill(105, 105, 105); //0x3300FF00
       fill(0xffff0000); // 0xff33ff99//0x3300FF00

      rect(sliderX, sliderY + height - height/4 + sliderHeight/2 , sliderWidth, sliderHeight);

      popMatrix();
      
   }
   
   
   public float getZoomValue(int x, int y) {
      if ((x >= minX) && (x <= (maxX - sliderWidth/2)) && (y > (height - height/3))) {
         sliderX = (int) (x - (sliderWidth/2));     
         if (sliderX < minX) { 
            sliderX = minX; 
         } 
         sliderValue = (x - minX) * valuePerX + minZoomValue;
      }     
      return sliderValue;
   }
   
   
   public void updateZoomSlider(float value) {
      int tempX = (int) (value / valuePerX) + minX;
      
      if ( (tempX >= minX) && (tempX <= (maxX+sliderWidth))  ) {
         sliderValue = value;
         sliderX = tempX;
      }
   }
   
   
/*   boolean isZoomSliderEvent(int x, int y) {
      int slop = 50;  // number of pixels above or below slider that's acceptable.  provided for ease of use.
      int sliderTop = (int) (sliderY - (sliderHeight/2)) - slop;
      int sliderBottom = sliderY + sliderHeight + slop;
      return ((x >= sliderX) && (x <= (sliderX    + sliderWidth)) && (y >= sliderTop)  && (y <= sliderBottom) || draggingZoomSlider );
   } */
   
      public boolean isZoomSliderEvent(int x, int y) {
      int slop = 50;  // number of pixels above or below slider that's acceptable.  provided for ease of use.
      int sliderLeft = (int) (sliderX - (sliderWidth/2)) - slop;
      int sliderRight = sliderX + sliderWidth + slop;
    //  return ((y >= sliderY + height - height/4) && (y <= (sliderY + height - height/4    + sliderHeight)) && (x >= sliderLeft)  && (x <= sliderRight) || draggingZoomSlider );
           return ((y >= sliderY + height - height/4 - sliderHeight/2) && (y <= (sliderY + height - height/4 + sliderHeight*2 )) && (x >= sliderLeft )  && (x <= sliderRight ) || draggingZoomSlider );
   } 
}
  public void settings() {  size( 650, 300, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "NACA4Series" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
