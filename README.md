# MultiTouchImageView

Extends Android's ImageView providing pinch- and double-tap-zoom.

API-Level 8 (Android 2.2 - Froyo) is required in order to use this view.

## Features:
* Pinch-Zoom
* Double-Tap-Zoom
* Easily customizable by inheritance (see advanced example)
* Reset the current state
* Keeps image in the view's bounds
* Zooming out gets your image back to the initial (centered) position

You can run this project as an Android application in order to see its behaviour. But genereally spoken it behaves just
like facebook's imageview.

## ToDo:
* Backwards compatibility down to 1.6
* Simplify scaling process ([affected code on github] (https://github.com/Taig/MultitouchImageView/blob/master/src/com/taig/widget/MultitouchImageView.java#L174),
[respective question on stackoverflow] (http://stackoverflow.com/questions/12749802/how-does-matrix-scaling-work))
* Provide smoother scrolling

[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/c03af8039f1657c81ce79a21ca6f96b4 "githalytics.com")](http://githalytics.com/Taig/MultiTouchImageView)
