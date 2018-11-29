

# NACA4Series
Processing code for generating NACA 4 digit airfoils
The program uses two sliders to adjust camber and thickness.  The highpoint is fixed at 40% chord for now.  The NACA4Series.png file is a screenshot of how the sliders work.  At first, all sliders are set to zero and all you get is a flat line.
If the only adjustment is to the vertical slider, then all you get is a camber line.  If the only adjustment is to the horizontal slider, then you will get a symmetrical NACA foil with no camber of course.  If you adjust both sliders, then you will get a NACA foil with camber and thickness.  There is some limit to what this program will properly display.  It seems ok under 15% camber and 15% thickness.  Above that, and it starts to break down visually.  This is a limitation of the author and not the NACA algorithm.


 I rewrote using built in processing functions for Arrays and coordinate manipulations which allows full range of thickness and camber through the sliders, without it breaking down visually.  Eventually, there will be a button for writing the coordinates to a file and adding some text to the screen to reflect the NACA 4 digit parameters.
