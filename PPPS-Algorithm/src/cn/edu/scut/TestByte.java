package cn.edu.scut;

public class TestByte {
    public static void main(String [] args){

        int x1 = 0x38;
        int r1 = 0x66;
        int res1 = (x1 - r1);
        for(int i = 0; i < 32; i++){
            System.out.print(res1 & 0x1);
            res1 = res1 >> 1;
        }

    }
}
