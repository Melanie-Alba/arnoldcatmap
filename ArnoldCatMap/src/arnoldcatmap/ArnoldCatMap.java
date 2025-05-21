/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package arnoldcatmap;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ArnoldCatMap {

    // Aplica una iteración del Arnold Cat Map a una imagen cuadrada
    public static BufferedImage applyArnoldMap(BufferedImage image) {
        int N = image.getWidth(); // Asumimos imagen cuadrada
        BufferedImage result = new BufferedImage(N, N, image.getType());

        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                int newX = (x + y) % N;
                int newY = (x + 2 * y) % N;
                result.setRGB(newX, newY, image.getRGB(x, y));
            }
        }

        return result;
    }

    // Compara dos imágenes píxel por píxel, y retorna el coeficiente de correlación
    public static double calculateCorrelation(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();

        long sum1 = 0, sum2 = 0;
        long sumSq1 = 0, sumSq2 = 0;
        long sumProduct = 0;
        int totalPixels = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = img1.getRGB(x, y) & 0xFF;
                int rgb2 = img2.getRGB(x, y) & 0xFF;

                sum1 += rgb1;
                sum2 += rgb2;
                sumSq1 += rgb1 * rgb1;
                sumSq2 += rgb2 * rgb2;
                sumProduct += rgb1 * rgb2;
            }
        }

        double numerator = sumProduct - (double) sum1 * sum2 / totalPixels;
        double denominator = Math.sqrt((sumSq1 - (double) sum1 * sum1 / totalPixels) *
                                       (sumSq2 - (double) sum2 * sum2 / totalPixels));

        return (denominator == 0) ? 0 : numerator / denominator;
    }

    // Compara si dos imágenes son iguales píxel a píxel
    public static boolean imagesAreEqual(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void main(String[] args) {
        try {
            String inputImagePath = "input.png";
            BufferedImage originalImage = ImageIO.read(new File(inputImagePath));

            BufferedImage currentImage = originalImage;
            BufferedImage firstOutput = null;
            BufferedImage mostScrambled = null;
            double lowestCorrelation = 1.0;
            int iterationOfMostScrambled = 0;
            int iteration = 0;

            while (true) {
                iteration++;
                currentImage = applyArnoldMap(currentImage);

                // Guarda la primera transformación
                if (iteration == 1) {
                    firstOutput = currentImage;
                    ImageIO.write(firstOutput, "png", new File("output_first.png"));
                    System.out.println("Primera imagen distorsionada guardada: output_first.png");
                }

                // Calcula correlación con la imagen original
                double corr = calculateCorrelation(originalImage, currentImage);

                if (corr < lowestCorrelation) {
                    lowestCorrelation = corr;
                    mostScrambled = currentImage;
                    iterationOfMostScrambled = iteration;
                }

                // Verifica si la imagen volvió a ser igual a la original
                if (imagesAreEqual(originalImage, currentImage)) {
                    ImageIO.write(currentImage, "png", new File("output_final.png"));
                    ImageIO.write(mostScrambled, "png", new File("output_most_scrambled.png"));

                    System.out.println("Imagen final restaurada: output_final.png");
                    System.out.println("Imagen mas distorsionada: output_most_scrambled.png (Iteracion " + iterationOfMostScrambled + ")");
                    System.out.println("Total de iteraciones hasta regresar a original: " + iteration);
                    break;
                }

                // Prevención de bucles por error
                if (iteration > 1000) {
                    System.out.println("Se supero el limite de iteraciones (1000). La imagen no volvio a su estado original.");
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}