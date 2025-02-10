package org.example;

public class Square {
    private float a;
    private float b;
    private float sq;

    public Square(int a, int b) {
        this.a = a;
        this.b = b;
        this.sq = sq;

        }
        public void math(){
        sq = 0;


        for (float del = 0.1f; (a+del)<b;a+=del){
            sq += Math.abs((((a*a*a+a+1)+((a+del)*(a+del)*(a+del)+(a+del)+1))*del)/2);
        }
        System.out.println(sq);

        }
        }

