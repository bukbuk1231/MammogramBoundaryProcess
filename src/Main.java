import java.awt.image.BufferedImage;

public class Main {

    public static void main(String[] args) {
        BufferedImage buffer = GreyScaleUtil.readImage("C:\\Users\\louda\\IdeaProjects\\Mammography\\assets\\mammogram-of-dense-breast-article.jpg");
        int[][] image = GreyScaleUtil.get2DImageArray(buffer);
        // GreyScaleUtil.print2DImageArray(image);
        // System.out.println(image.length + " , " + image[0].length);
        Mammograph mammo = new Mammograph(image);
    }
}
