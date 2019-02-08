ImageJ plugin auto_qc

Created by group 4 of the NEUBIAS trainingschool 11.

Erick Ratamero, Cesar Augusto Valades Cruz, Christopher Schmied, Jan Eckhardt, Paul Stroe.

Based on the .py script auto_PSF.py from Erick Ratamero:
https://github.com/erickmartins/autoQC/blob/master/auto_PSF.py


IMPORTANT: Depends on a .jar from MetroloJ!
Please download from the website:
http://imagejdocu.tudor.lu/doku.php?id=plugin:analysis:metroloj:start

==========================================================================================
Citations:

Based on ImageJ:
Schneider, C. A.; Rasband, W. S. & Eliceiri, K. W. (2012), "NIH Image to ImageJ: 25 years of image analysis", Nature methods 9(7): 671-675, PMID 22930834

Used to run Fiji in the background:
Schindelin, J.; Arganda-Carreras, I. & Frise, E. et al. (2012), "Fiji: an open-source platform for biological-image analysis", Nature methods 9(7): 676-682, PMID 22743772, doi:10.1038/nmeth.2019

Uses the following plugins:
bioformats (https://www.openmicroscopy.org/bio-formats/)
Melissa Linkert, Curtis T. Rueden, Chris Allan, Jean-Marie Burel, Will Moore, Andrew Patterson, Brian Loranger, Josh Moore, Carlos Neves, Donald MacDonald, Aleksandra Tarkowska, Caitlin Sticco, Emma Hill, Mike Rossner, Kevin W. Eliceiri, and Jason R. Swedlow (2010) Metadata matters: access to image data in the real world. The Journal of Cell Biology 189(5), 777-782. doi: 10.1083/jcb.201004104

MetroloJ (http://imagejdocu.tudor.lu/doku.php?id=plugin:analysis:metroloj:start)
Cédric Matthews and Fabrice P. Cordelieres, MetroloJ : an ImageJ plugin to help monitor microscopes' health, in ImageJ User & Developer Conference 2010 proceedings

==========================================================================================
Acknowledgements

Special thanks goes to:
Robert Haase
Jean-Yves Tinevez
For their help and guidance

NEUBIAS and NEUBIAS TS11 organisers for creating the environment

==========================================================================================

Title: Automatic PSF quality measurement

Input file: a file of the specified file format (file extension) containing the string "psf"

Example data used for testing this was named 100_psf_auxin_sf01_R3D.dv

Dimension: 1024x1024x21 16Bit


Pixelsize: 0.04031x0.04031x0.15 micron (Metadata matters)

Content: PSFs from flourescent beads
         The usual PSF measurement criteria apply:
         Well separated beads, not too dense, subresolution.

Output: summary table with quality measurements per bead
        Will be written into the base input directory

Input parameters - mostly hardcoded for now

- Input file - GUI interaction that points to any file where the input files are
    From this the base directory is extracted

- File extension: self-explanatory

- Number of beads: how many beads should be analysed

- Correction factors: multiplying factor for the FWHM resolution output from MetroloJ, per dimension

- minSeparation: minimal distance (probably in pixels?) that two PSFs need to be separated by to be considered

Description:

Finds the relevant stack within the specified diretory that contains the bead measurement.
Opens the stack using bioformats importer.
Crops the stack to get the center of the field of view.
Gets the slice that is in focus (max standard deviation).
Detects the beads via maximum detection.
Takes the location of the point detection.
Extracts the intensity of the pixel at that point.
Selects the specified number of beads with the lowest intensity that are well separated.
Crops out these selected beads from the stack.
Applies PSFprofiler of the MetroloJ plugin to get resolution of microscope.

Functions of the plugin:

run:
Opens dialog for choosing a file.
Gets based path of file.

readFile:
Opens file using Bioformats import.
Calls processing function.
Specifies result file.
Calls WriteFile to save result table.

processing:
Crops the image to get middle of the field of view.
Finds the in Focus slice via maximum standard deviation.
Detects maxima in the in Focus slice.
Gets coordinates of ROIs and gets Pixel Value at this coordinate.
Sorts the Pixel coordinates by the intensity value.
Selects beads based on specified criteria.
Crops out the PSFs
Calls GetRes function to get Resolution values.
Multiplies the resolution values with the correction factors.

WriteFile:
Writes the result files with header

GetRes:
Calls the PSF Profiler of MetroloJ on cropped PSF

----------------------------------------------------------------------------------------
ToDo:

User interaction with GUI

Images are shown at the moment (batchmode?)

Close opened images

Documentation of the code

Refactor

Clean up code



