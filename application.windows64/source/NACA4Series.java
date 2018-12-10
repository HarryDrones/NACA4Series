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

// https://discourse.processing.org/t/processing-to-draw-naca-4-digit-airfoils/5739
// http://www.airfoiltools.com/airfoil/naca4digit?MNaca4DigitForm%5Bcamber%5D=05&MNaca4DigitForm%5Bposition%5D=40&MNaca4DigitForm%5Bthick%5D=30&MNaca4DigitForm%5BnumPoints%5D=81&MNaca4DigitForm%5BcosSpace%5D=0&MNaca4DigitForm%5BcosSpace%5D=1&MNaca4DigitForm%5BcloseTe%5D=0&MNaca4DigitForm%5BcloseTe%5D=1&yt0=Plot
// http://www.pdas.com/refs/tm4741.pdf   //different calc
// https://discourse.processing.org/t/trying-to-use-modelx-but-built-a-spiral-galaxy-of-points/6064
// https://discourse.processing.org/t/trying-to-use-modelx-but-built-a-spiral-galaxy-of-points/6064/12

int N = 81;  //number of points
float M = -0.00000f; //2/100.0;
float[] dydx = new float[81];
float T = 0/100.0f;
float[] dT;
float x[] = new float[81];  //divide up unit chord length by N
float y[] = new float[81];  //divide up unit chord length by N
float xU[] = new float[81];
float yU[] = new float[81];
float xL[] = new float[81];
float yL[] = new float[81];
float P = 0.4f; //0.4; //40/100.0;
PShape s,s2;
PFont font;
float a0 = 0.2969f;
float a1 = -0.126f;
float a2 = -0.3516f;
float a3 = 0.2843f;
float a4 = -0.1036f;
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
public void save_to_file() {
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

public void keyPressed() {
  if ( key == 's' )  save_to_file();
}

public void mouseWheel(MouseEvent event) {
  float e = event.getCount();
 // if ( keyPressed )  P += 0.01*e; //&& key == 'p' )  P += 0.01*e; 
  P += 0.01f*e;
}

public void setup()
{
  
   font = loadFont("CourierNew36.vlw"); 
 
 controls = new Controls();
 controlX = new HorizontalControl();
 showControls = 1;     
 getOrdinates();
 println ("done.");
}

 public void draw() {
   

    if (mousePressed) {
     if( (showControls == 1) && (controls.isZoomSliderEvent(mouseX, mouseY)) || ( showControls == 1 && controlX.isZoomSliderEvent(mouseX,mouseY))) {
         draggingZoomSlider = true;     
         zoom = controls.getZoomValue(mouseY);
         tzoom = controlX.getZoomValue(mouseX,mouseY);      
         M = map(M,-0.10f,0.10f,-zoom,zoom); //,-0.10,0.10);
         T = map(T,0,12,-tzoom,tzoom);
     }    
           // MousePress - Rotation Adjustment
      else if (!draggingZoomSlider) {
         if (released == true) {
    
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
  scale(1.5f,-1.5f);
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


public void getOrdinates(){
   
  for (int i = 0; i < N; i++){
  
    M = zoom/2500f;
    T = tzoom/2500f;

    if(x[i] >= 0){
       beta[i] = (radians(180)/81.0f) * i;
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
   
       yt[i] = (T/0.2f* (a0*sqrt(x[i])+ a1*x[i] + a2*(x[i]*x[i]) + a3*(x[i]*x[i]*x[i]) + a4*(x[i]*x[i]*x[i]*x[i])));
       theta[i] = (atan((dydx[i])));
       xU[i] = x[i] - yt[i] * (sin(radians(theta[i]))); 
       yU[i] = ( (y[i] + yt[i]  * (cos(radians(theta[i]))) ) );
       xL[i] = x[i] + yt[i] * sin(radians(theta[i]));        
       yL[i] = (y[i] - yt[i] * cos(radians(theta[i]))); 
         
    }
    


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
