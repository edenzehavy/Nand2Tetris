// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// Assumes that R0 >= 0, R1 >= 0, and R0 * R1 < 32768.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

//R2=0
@R2
M=0
//if (R1==0) goto END
(LOOP)
@R1
D=M
@END
D;JEQ
//R2=R2+R0
@R0
D=M
@R2
M=D+M
//R1 = R1-1
@R1
M=M-1
@LOOP
0;JMP
(END)
@END
0;JMP


