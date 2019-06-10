# PosterProjector3000

Project anything you want onto your posters!

![Alt text](https://i.imgur.com/MYQm0UN.jpg)

Here are some examples of what it can do https://imgur.com/a/RD5Hks5

# Installation 

Simply compile and run the java files in the repository.

```
javac *.java
java Main
```

Or use the built in run script.
```
sh ./run.sh
```

# Setup

1. Mirror your primary display to your projector. (Optional but recommended)
2. Aim you projector so that it fully covers your posters. (It does not need to be aligned perfectly)
3. Run PosterProjector3000 (The screen should turn fully black. (You may need to hide taskbars manually 
to achieve seamless use).
4. While looking at your posters move your mouse to click on each corner as seen below. (Backspace will
 undo your last change)
 
 ```
1_____2    5_____6      A_____B   A = 4(n-1) + 1
|pster|    |pster|      |pster|   B = 4(n-1) + 2
|  1  |    |  2  | .... |  n  |   C = 4(n-1) + 3
|     |    |     |      |     |   D = 4(n-1) + 4
4_____3    8_____7      D_____C
```


# Transparent Mode

NOTE : Transparent mode may not work on some Operating Systems / Desktop Environments (Working on XFCE,Gnome)

Transparent mode allows for anything ( Video / Image / Simulation / Website / Etc.) to be projected onto your posters. 

## Usage

Press the space bar to cycle modes: (Transparent 1, Transparent 2, Calibration ). 

All hotkeys except Space and Escape are disabled in transparent mode. 
```
Transparent 1
 Dark Area - Unchanged
 Poster Area - Transparent
Transparent 2
 Dark Area - Transparent
 Poster Area - Unchanged
Calibration
 Dark Area - Unchanged.
 Poster Area - Unchanged
```

# Controls
```
MOUSE LEFT : Trace Poster
SPACE : Cycle Transparent Mode
BACKSPACE : Undo
ESC : Quit
S : Save as default
D : Load default
F : Delete all
H : Toggle help
```
 
