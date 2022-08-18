package cn.edu.scut;

public class TestZxl {
    public static void main(String[] args) {
        byte[][] demo = new byte[][]{{100, -89},{44,22},{2,3},{4,11},{55,23},{32,11}};
        byte[][] arrayPress1 = getFirstPress(demo,demo.length,demo[0].length) ;
        for (int i = 0; i < demo.length; i++) {
            for (int i1 = 0; i1 < demo[i].length; i1++) {
                System.out.print(demo[i][i1]+" ");
            }
        }
        System.out.println(" ");
        byte[] arrayPress2 = getSecondPress(arrayPress1, arrayPress1.length, arrayPress1[0].length );
        for (int i = 0; i < arrayPress2.length; i++) {
            System.out.print(arrayPress2[i]+" ");
        }
        System.out.println(" ");
        byte [] arrayPress3 = getThirdPress(arrayPress2);
        for (int i = 0; i < arrayPress3.length; i++) {
            System.out.print(arrayPress3[i]+" ");
        }
        System.out.println(" ");
        byte press4 = getForthPress(arrayPress3);
        System.out.println(press4);
    }

    public static byte[][] getFirstPress(byte [][]array,int row, int col ){
        for (int i = 0; i < row; i++) {
            for (int i1 = 0; i1 < col; i1++) {
                int result = 0;
                for(int i2 = 0; i2<8; i2++) {

                    byte temp = 0b00000001;
                    int point = array[i][i1]&(temp<<(i2%8));
                    if(point != 0){
                        result++;
                    }
                }
                array[i][i1] = (byte)result;
            }
        }
        return array;
    }

    public static byte[] getSecondPress(byte [][]array, int row, int col){
        byte []press2 = new byte[array.length];
        for(int i = 0; i < row; i++){
            for(int i1 = 0; i1 < col; i1++){
                press2[i] += array[i][i1];
            }
        }
        return press2;
    }

    public static byte[] getThirdPress(byte []array){
        byte []press3 = new byte[array.length/2];
        for (int i = 0; i < press3.length; i++){
            press3[i] += array[i*2] + array[i*2 + 1];
        }
        return press3;
    }
    public static byte getForthPress(byte []array){
        byte result = 0;
        for (int i = 0; i < array.length; i++) {
            result += array[i];
        }
        return result;
    }
}
