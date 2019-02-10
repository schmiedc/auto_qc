/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package lu.neubias.imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;

import java.io.FileWriter;
import java.io.IOException;

import ij.plugin.Duplicator;
import ij.process.FloatPolygon;
import loci.formats.FormatException;
import loci.plugins.BF;

import metroloJ.resolution.PSFprofiler;
import net.imagej.ImageJ;



import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.NumberWidget;
import org.scijava.widget.Button;


import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


/**
*For User Input check: https://github.com/imagej/tutorials/blob/master/maven-projects/widget-demo/src/main/java/WidgetDemo.java
*
 */


@Plugin(type = Command.class, menuPath = "Plugins>Quality control>Autoqc")
public class autoqc extends Component implements Command  {





    @Parameter(label = "File extension (starting with a dot)")
    private String ext = ".dv";

    @Parameter
    private File file;

    @Parameter(label = "#Beads to analyze",
            style = NumberWidget.SPINNER_STYLE, min = "1", max = "20")
    private Integer beads=3;

    @Parameter(label = "Correction Factor X")
    private double corr_factor_x = 1.186;
    @Parameter(label = "Correction Factor Y")
    private double corr_factor_y = 1.186;
    @Parameter(label = "Correction Factor Z")
    private double corr_factor_z = 1.186;

    @Parameter(label = "Minimal separation",
            style = NumberWidget.SPINNER_STYLE, min = "1", max = "30")
    private int minSeparation = 15;



    //String ext = ".dv";
    //int beads = 1;
   /* double corr_factor_x = 1.186;
    double corr_factor_y = 1.186;
    double corr_factor_z = 1.186;*/
    //int minSeparation = 15;


    // You can control how previews work by overriding the "preview" method.
    // The code written in this method will be automatically executed every
    // time a widget value changes. In this case, we log a message with the
    // count of previews so far, and show the current value of the "message"
    // parameter in the ImageJ status bar.



    private void append(final StringBuilder sb, final String s) {
        sb.append(s + "\n");
    }
    public void run() {




       // JFileChooser chooser = new JFileChooser();
        //int returnVal= (int) chooser.showOpenDialog(this);

        //chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

       /* File selectedDir = chooser.getCurrentDirectory();
        File selectedFile = chooser.getSelectedFile();*/

        File selectedDir = file.getParentFile();
        String selectedFile = file.getName();

        String srcDir = selectedDir.getAbsolutePath();

        System.out.println(srcDir);
        System.out.println("File: " + selectedFile);

        ArrayList<String> folders = new ArrayList<String>();
        ArrayList<String> filenames = new ArrayList<String>();



        for (final File fileEntry : selectedDir.listFiles()){

            if (fileEntry.getName().endsWith(ext)&&fileEntry.getName().contains("psf")){

                System.out.println("Processing file: " + fileEntry.getName());
                String path = selectedDir + File.separator + fileEntry.getName();

                readFile(path, selectedDir);
            }




        }


        // skip irrelevant filenames, do stuff for relevant ones


    }

    public void readFile(String arg, File selectDir) {

        //  OpenDialog od = new OpenDialog("Open Image File...", arg);
        //  String dir = od.getDirectory();
        //  String name = od.getFileName();
        //  String id = dir + name;
        long[] dimensions = new long[]{
                512, 512
        };

       // Img<FloatType> imgFinal = ArrayImgs.floats(dimensions);

        ImagePlus[] imps = new ImagePlus[0];

        ImagePlus imp = new ImagePlus();
        try {

            imps = BF.openImagePlus(arg);

            imp = imps[0];
            //imgFinal = ImageJFunctions.convertFloat(imps[0]);
            // ij.ui().show(imgFinal);
            // for (ImagePlus imp : imps) imp.show();
            // We don't need to show them

        } catch (FormatException exc) {

            IJ.error("Sorry, an error occurred: " + exc.getMessage());

        } catch (IOException exc) {

            IJ.error("Sorry, an error occurred: " + exc.getMessage());

        }

        double[][] finalResult = processing(imp);

        String resultPath = selectDir + File.separator + "summary.csv";

        WriteFile(resultPath,finalResult);



    }

    public double[][] processing(ImagePlus image){

        IJ.run("Set Measurements...", "min centroid integrated redirect=None decimal=3");
        System.out.println("Opened file, processing");
        image.show();

        // Crops the image to get middle of the field of view
        IJ.run("Specify...", "width=300 height=300 x=512 y=512 slice=1 centered");
        IJ.run("Crop");

        ImagePlus croppedImage = IJ.getImage();

        float[] stats= new float[croppedImage.getNSlices()];

        int slicePos = 1;

        float maxStd = -1;

        // loops over stack Finds the in Focus slice
        for (int i=1; i < croppedImage.getNSlices(); i++){

            ImagePlus myslice = new Duplicator().run(croppedImage, i, i);

           double aux = myslice.getStatistics().stdDev;

            if (aux > maxStd){
                maxStd = (float) aux;
                slicePos=i;

            }

            System.out.println("In focus slice: " + slicePos);

        }

        ImagePlus infocusSlice = new Duplicator().run(croppedImage, slicePos, slicePos);


       infocusSlice.show();


        // detect beads and measure for intensity and x/y coords
        IJ.run("Find Maxima...", "noise=30 output=[Point Selection] exclude");

        // Gets coordinates of ROIs
        Roi roi = infocusSlice.getRoi();
        FloatPolygon floatPolygon = roi.getFloatPolygon();

        float[][] resultsTable = new float[floatPolygon.npoints][3];

        // loops over ROIs and get pixel Vvlue at their coordinate.
        for (int i=1; i < floatPolygon.npoints; i++){

            float intx = floatPolygon.xpoints[i];
            float inty = floatPolygon.ypoints[i];

            int[] pixel = infocusSlice.getPixel( (int) intx, (int) inty);

            resultsTable[i-1][0] = intx;
            resultsTable[i-1][1] = inty;
            resultsTable[i-1][2] = (float) pixel[0];

        }
        infocusSlice.hide();
        System.out.println("ResultTable size " + resultsTable.length);

        // Sorts the Pixel coordinates by the intensity value.
        java.util.Arrays.sort(resultsTable, new java.util.Comparator<float[]>() {
                    public int compare(float[] a, float[] b) {

                        return Double.compare(a[0], b[0]);

                    }
                });

        int countSpots = 0;
        int firstPosition = 1;
        double goodX[] = new double[beads];
        double goodY[] = new double[beads];

        System.out.println("Created table of size " + resultsTable.length);

        // selects the selected number of pixels based on the specified criteria
        while (countSpots < beads && firstPosition < resultsTable.length ){

            float x1 = resultsTable[firstPosition][0];

            float y1 = resultsTable[firstPosition][1];
            System.out.println("First Pos: " + x1 + " " + y1);

            int nextPosition = firstPosition + 1;
            boolean valid = true;

            while (valid && nextPosition < resultsTable.length){

                float x2 = resultsTable[nextPosition][0];
                float y2 = resultsTable[nextPosition][1];

                double dist_sq = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);

                if (x2 != x1 && y2 != y1 && dist_sq < Math.pow(minSeparation,2)){

                    valid = false;

                }

                nextPosition++;


            }

            if (valid){
                goodX[countSpots] = resultsTable[firstPosition][0];
                goodY[countSpots] = resultsTable[firstPosition][1];
                countSpots++;

            }

            firstPosition++;

        }

        System.out.println("Spot 1: " + goodX[0] + " " + goodY[0]);
        System.out.println("Spot 2: " + goodX[1] + " " + goodY[1]);
        System.out.println("Spot 3: " + goodX[2] + " " + goodY[2]);


        double[][] finalResults  = new double[beads][3];

        // loops over selected pixels and crops out the PSFs
        for (int i = 0; i < goodX.length; i++){

            ImagePlus PSFimage = new Duplicator().run(croppedImage, 1, croppedImage.getNSlices());
            PSFimage.show();

            // crops stack around the specified coordinates
            IJ.run(PSFimage, "Specify...", "width=20 height=20 x=" + goodX[i] + " y=" + goodY[i] + " slice=1 centered");
            IJ.run("Crop");
            ImagePlus croppedPSF = IJ.getImage();
            PSFimage.hide();
           croppedImage.show();

            // calls GetRes to extract the resolution form the PSFs
            double[] qcMetrics = GetRes(croppedPSF);
            croppedImage.hide();
            System.out.println("QC values " + qcMetrics[0]);
            System.out.println("QC values " + qcMetrics[1]);
            System.out.println("QC values " + qcMetrics[2]);

            // multiply by the correction factor
            double xRes = qcMetrics[0] * corr_factor_x;
            double yRes = qcMetrics[1] * corr_factor_y;
            double zRes = qcMetrics[2] * corr_factor_z;

            finalResults[i][0] = xRes;
            finalResults[i][1] = yRes;
            finalResults[i][2] = zRes;

        }

        System.out.println("Final Results " + Arrays.deepToString(finalResults));

        return finalResults;

    }


    public static boolean WriteFile(String FilePath, double[][] BeatResArray){

            String COMMA_DELIMITER = ",";
            String NEW_LINE_SEPARATOR = "\n";
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(FilePath);
                //Write the CSV file header
                fileWriter.append("x_resolution"+COMMA_DELIMITER+"y_resolution"+COMMA_DELIMITER+"z_resolution");
                //Add a new line separator after the header
                fileWriter.append(NEW_LINE_SEPARATOR);
                int Datalength = BeatResArray.length;
                for (int x = 0; x <Datalength; x++)
                {

                    fileWriter.append(String.valueOf(BeatResArray[x][0]));
                    fileWriter.append(COMMA_DELIMITER);
                    fileWriter.append(String.valueOf(BeatResArray[x][1]));
                    fileWriter.append(COMMA_DELIMITER);
                    fileWriter.append(String.valueOf(BeatResArray[x][2]));
                    fileWriter.append(NEW_LINE_SEPARATOR);

                }
            } catch (Exception e) {

                System.out.println("Error in CsvFileWriter !!!");
                e.printStackTrace();

            } finally {

                try {

                    fileWriter.flush();
                    fileWriter.close();

                } catch (IOException e) {

                    System.out.println("Error while flushing/closing fileWriter !!!");
                    e.printStackTrace();

                }
            }
            return true;

    }

    public static double[] GetRes(ImagePlus BeatStack){

        PSFprofiler profiler=new PSFprofiler(BeatStack);
        return profiler.getResolutions();

    }


    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        //final ImageJ ij = new ImageJ();
        //ij.ui().showUI();

        // ask the user for a file to open
        //final File file = ij.ui().chooseFile(null, "open");

        //if (file != null) {
            // load the dataset
           // final Dataset dataset = ij.scifio().datasetIO().open(file.getPath());

            // show the image
           // ij.ui().show(dataset);

            // invoke the plugin
           // ij.command().run(autoqc.class, true);





        final ImageJ ij = new ImageJ();
        ij.launch(args);
        //ij.ui().showUI();

        ij.command().run(autoqc.class, true);
       /* autoqc test = new autoqc();
        test.run();*/
        }
    }


