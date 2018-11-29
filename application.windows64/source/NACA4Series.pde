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

void setup()
{
 size( 650, 300, P3D);   
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
         M = map(M,-0.10,0.10,-zoom,zoom); //,-0.10,0.10);
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

    if (showControls == 1) {
     controls.render(); 
         controlX.render(); 
    }
  controls.updateZoomSlider(zoom);
  controlX.updateZoomSlider(tzoom);

  translate(width/2-450/2, height/2);
  scale(1.5,-1);
  thread( "getOrdinates");
  stroke(0,255,0);
  strokeWeight(4);
  Xvalues = concat(xU,xL);
  Yvalues = concat(yU,yL);
  XLoord = subset(Xvalues,81,80);
  YLoord = subset(Yvalues,81,80);
   s = createShape(); 
   s.beginShape();
// s.fill(125);
   s.noFill();
   s.stroke(0,255,0);
   s.strokeWeight(2);

        for (int i = 0; i<80; i++){ 
           s.vertex(300*XLoord[i],300*YLoord[i]);
       
           angle = sqrt( ((300 - Xvalues[0]) * (300 - Xvalues[0])) + ((300*Yvalues[80]) * (300*Yvalues[80])) );
           angle = asin(300*Yvalues[80]/angle);
        }
        XLoord = reverse(XLoord);
        YLoord = reverse(YLoord);
        for (int i = 0; i<80; i++){ 
           s.vertex(300*XLoord[i],300*YLoord[i]);
       
           angle = sqrt( ((300 - Xvalues[0]) * (300 - Xvalues[0])) + ((300*Yvalues[80]) * (300*Yvalues[80])) );
           angle = asin(300*Yvalues[80]/angle);
        }
               
   s.endShape(CLOSE);
   shearY(angle); 
   shape(s,0,0);
   shearY(-angle);

   XUoord = subset(Xvalues,0,80);
   YUoord = subset(Yvalues,0,80);
   s2 = createShape(); 
   s2.beginShape();
// s.fill(125);
   s2.noFill();
   s2.stroke(0,255,0);
   s2.strokeWeight(2);


        for (int i = 0; i<80; i++){ 
           s2.vertex(300*XUoord[i],300*YUoord[i]);
       
           angle = sqrt( ((300 - Xvalues[0]) * (300 - Xvalues[0])) + ((300*Yvalues[80]) * (300*Yvalues[80])) );
           angle = asin(300*Yvalues[80]/angle);
        } 
        XUoord = reverse(XUoord);
        YUoord = reverse(YUoord);
               for (int i = 0; i<80; i++){ 
           s2.vertex(300*XUoord[i],300*YUoord[i]);
        
           angle = sqrt( ((300 - Xvalues[0]) * (300 - Xvalues[0])) + ((300*Yvalues[80]) * (300*Yvalues[80])) );
           angle = asin(300*Yvalues[80]/angle);
        }
          
   s2.endShape(CLOSE);
   shearY(-angle); 
   shape(s2,0,0);

        
}


void getOrdinates(){
   
  for (int i = 0; i < N; i++){
  
    M = zoom/1000f;
    T = tzoom/1000f;

    if(i >= 0){
       beta[i] = (radians(180)/81.0) * i;
       x[i] = (1 - cos(beta[i]))/2;
     }
    if(i < 40){
     if (x[i] >= 0){
     y[i] = ((M/(P*P) * (2*P*x[i] - x[i]*x[i])));  // /1.005;
     dydx[i] = (2*M)/(P*P) * (P - x[i]);
     }    
   }  
    if(i >=40){  
    if (x[i] <= 1.00000f){     
    y[i] = (M/(1-P)*(1-P) * (1 - 2*P + 2*P*x[i] - x[i]*x[i]))*2.725; //* 2.75; //*2.686; 
    dydx[i] = (2*M/((1-P)*(1-P)) * (P - x[i])); 
     } 
   }    
}  //end for loop

   for (int i = 0; i < 81; i++){
   
       yt[i] = (T/0.2* (sqrt(a0*x[i])+ a1*x[i] + a2*(x[i]*x[i]) + a3*(x[i]*x[i]*x[i]) + a4*(x[i]*x[i]*x[i]*x[i])));
       theta[i] = (atan((dydx[i])));
       xU[i] = x[i] - yt[i] * (sin(radians(theta[i]))); 
       yU[i] = ( (y[i] + yt[i]  * (cos(radians(theta[i]))) ) );
       xL[i] = x[i] + yt[i] * sin(radians(theta[i]));        
       yL[i] = (y[i] - yt[i] * cos(radians(theta[i]))); 
         
    }

}
