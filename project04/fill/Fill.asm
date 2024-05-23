// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen
// by writing 'black' in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen by writing
// 'white' in every pixel;
// the screen should remain fully clear as long as no key is pressed.

//Sets counter
(STARTB)
@8191
D=A
@R1
M=D

//if @KBD = 0 goto STARTW
(BLACK)
@KBD
D=M
@STARTW
D;JEQ

//Colors the current pixel black
@R1
D=M
@SCREEN
A=A+D
M=-1

//If R1==0 goto STARTB
@R1
D=A
@STARTB
D;JEQ

//Decrement the counter by 1
@R1
M=M-1

//goto BLACK
@BLACK
0;JMP

//Reset counter
(STARTW)
@8191
D=A
@R1
M=D

//Colors the current pixel white
(WHITE)
@R1
D=M
@SCREEN
A=A+D
M=0

//if (R1==0) goto STARTB
@R1
D=M
@STARTB
D;JEQ

//Decrement the counter by 1
@R1
M=M-1

@WHITE
0;JMP