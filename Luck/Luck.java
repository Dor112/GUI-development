package org.example;

public class Luck {
    private int x;
    private int y;
    private int z;
    private int m;
    private int n;
    private int k;
    private int t;

    public Luck() {
        this.x = x;
        this.y = y;
        this.z = z;
        this.m = m;
        this.n = n;
        this.k = k;
        this.t = t;

        }
        public void lucky(){
            for(int x=0; x<10; x++){
                for(int y=0; y<10; y++){
                    for(int z=0; z<10; z++){
                        for(int m=0; m<10; m++){
                            for(int n=0; n<10; n++){
                                for(int k=0; k<10; k++){
                                    if((x+y+z) == (m+n+k)){
                                        t+=1;
                                    }

                                }
                            }
                        }
                    }
                }
            }
            System.out.println(t-1);
        }
}
