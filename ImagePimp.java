/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Bruce
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.awt.image.ConvolveOp;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.ImageIO;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ImagePimp extends JFrame{
    public Container contentPane;
    public static JDesktopPane desktopPane;

    public ImagePimp(){
	super("ImagePimp");
	initializeFrameWindow();
    }

    private void initializeFrameWindow(){
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
	desktopPane = new JDesktopPane();
        contentPane.add(desktopPane);
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
    	JMenuItem newFileMenuItem = new JMenuItem("New");
        JMenuItem openFileMenuItem = new JMenuItem("Open");
	JMenuItem saveFileMenuItem = new JMenuItem("Save");
	JMenuItem saveAsFileMenuItem = new JMenuItem("Save As...");
	JMenuItem closeFileMenuItem = new JMenuItem("Close");

        newFileMenuItem.setEnabled(false);
	saveFileMenuItem.setEnabled(false);

	openFileMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileChooser.showOpenDialog(ImagePimp.this) != JFileChooser.CANCEL_OPTION){
                    ImageFrame imageFrame=new ImageFrame(fileChooser.getSelectedFile());
                    desktopPane.add(imageFrame);
                    imageFrame.addMouseMotionListener(new MouseInputAdapter());
                    imageFrame.addMouseListener(new MouseInputAdapter());
                    imageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                    try{imageFrame.setSelected(true);}
                    catch(Exception e){}
                }
            }
        });

        saveAsFileMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (fileChooser.showSaveDialog(ImagePimp.this) != JFileChooser.CANCEL_OPTION){
                    String path = fileChooser.getSelectedFile().getPath();
                    if(!path.contains(".jpg")){
                        if(path.contains(".")){
                            path = path.substring(0, path.indexOf("."));
                        }
                        path = path+".jpg";
                    }
                    ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                    Dimension dimension = getImageDimension(imageFrame.getImage());
                    BufferedImage bufferedImage = new BufferedImage((int)dimension.getWidth(), (int)dimension.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = bufferedImage.createGraphics();
                    g.drawImage(imageFrame.getImage(), null, null);
                    File outputFile = new File(path);
                    try {ImageIO.write(bufferedImage, "JPG", outputFile);}
                    catch (IOException ex) {Logger.getLogger(ImagePimp.class.getName()).log(Level.SEVERE, null, ex);}
		}
            }
        });

        closeFileMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
		System.exit(0);
            }
	});

	fileMenu.add(newFileMenuItem);
	fileMenu.add(openFileMenuItem);
        fileMenu.add(saveFileMenuItem);
	fileMenu.add(saveAsFileMenuItem);
	fileMenu.addSeparator();
	fileMenu.add(closeFileMenuItem);
        menuBar.add(fileMenu);

        JMenu transformationsMenu = new JMenu("Transformations");
        JMenuItem ainetTransformationsMenuItem = new JMenuItem("Ainet");
	JMenuItem grayscaleTransformationsMenuItem = new JMenuItem("Grayscale");
        JMenuItem histogramTransformationMenuItem = new JMenuItem("Histogram");
	JMenuItem contrastStretchingMenuItem = new JMenuItem("Contrast Stretching");
        JMenuItem customTransformationsMenuItem = new JMenuItem("Custom...");
	customTransformationsMenuItem.setEnabled(false);
        JMenu edgeDetectMenu = new JMenu("Edge Detection");
        JMenuItem derivativeFilterMenuItem = new JMenuItem("Derivative Fiter");
        JMenuItem robertsMenuItem = new JMenuItem("Roberts Operator");
        JMenuItem smoothMenuItem = new JMenuItem("Smooth");
        edgeDetectMenu.add(derivativeFilterMenuItem);
        edgeDetectMenu.add(robertsMenuItem);
        JMenu lineDetectMenu = new JMenu("Line Detection");
        JMenuItem houghItem = new JMenuItem("Hough Transform");
        lineDetectMenu.add(houghItem);
        JMenuItem sharpenMenuItem = new JMenuItem("Sharpen");
	JMenu segmentationMenu = new JMenu("Segmentation");
        JMenuItem kMeans = new JMenuItem("K-Means");
        JMenuItem fuzzyCMeans = new JMenuItem("Fuzzy C-Means");
        JMenuItem possibilityCMeans = new JMenuItem("Possibility C-Means");
        segmentationMenu.add(kMeans);
        segmentationMenu.add(fuzzyCMeans);
        segmentationMenu.add(possibilityCMeans);
	JMenu rotateSubmenu = new JMenu("Rotate");
        JMenuItem nintyMenuItem = new JMenuItem("90 Degrees");
        JMenuItem oneEightyMenuItem = new JMenuItem("180 Degrees");
        JMenuItem twoSeventyMenuItem = new JMenuItem("270 Degrees");
        rotateSubmenu.add(nintyMenuItem);
        rotateSubmenu.add(oneEightyMenuItem);
        rotateSubmenu.add(twoSeventyMenuItem);
        //////////////////////////////////
        //JMenu My_transformationsMenu = new JMenu("My_Transformations");
        //JMenuItem My_contrastStretchingMenuItem = new JMenuItem(" Automated Contrast Stretching");

        ainetTransformationsMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
		try{
                    ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                    ImageFrame newImageFrame = new ImageFrame(ainet(imageFrame.getImage()));
                    desktopPane.add(newImageFrame);
                    newImageFrame.toFront();
                    newImageFrame.setTitle("Ainet");
                    newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                    newImageFrame.addMouseListener(new MouseInputAdapter());
                    newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                    try{newImageFrame.setSelected(true);}
                    catch(Exception e){}
                }
                catch(Exception ex){Logger.getLogger(ImagePimp.class.getName()).log(Level.SEVERE, null, ex);
}
            }
	});

        nintyMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame = new ImageFrame(rotate90(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("90 degree Rotation");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
		}
	});

        oneEightyMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
		ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame = new ImageFrame(rotate180(imageFrame.getImage()));
		desktopPane.add(newImageFrame);
		newImageFrame.toFront();
                newImageFrame.setTitle("180 degree Rotation");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
        });

        twoSeventyMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
		ImageFrame newImageFrame = new ImageFrame(rotate270(imageFrame.getImage()));
		desktopPane.add(newImageFrame);
		newImageFrame.toFront();
                newImageFrame.setTitle("270 degree Rotation");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});

        grayscaleTransformationsMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
		ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
		ImageFrame newImageFrame = new ImageFrame(colorToGrayscale(imageFrame.getImage()));
		desktopPane.add(newImageFrame);
		newImageFrame.toFront();
                newImageFrame.setTitle("Gray Scale");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});

        histogramTransformationMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
		ImageFrame newImageFrame = new ImageFrame(histogram(imageFrame.getImage()));
		desktopPane.add(newImageFrame);
		newImageFrame.toFront();
                newImageFrame.setTitle("Histogram Equalization");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});

        derivativeFilterMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame = new ImageFrame(derivativeFilter(imageFrame.getImage()));
		desktopPane.add(newImageFrame);
		newImageFrame.toFront();
                newImageFrame.setTitle("Derivative Filter Edge Detection");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		try{newImageFrame.setSelected(true);}catch(Exception e){}
            }
	});

        robertsMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame = new ImageFrame(robertsOperator(imageFrame.getImage()));
		desktopPane.add(newImageFrame);
		newImageFrame.toFront();
                newImageFrame.setTitle("Robert's Operator Edge Detection");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		try{newImageFrame.setSelected(true);}catch(Exception e){}
            }
	});

        houghItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame = new ImageFrame(houghTransform(imageFrame.getImage()));
		desktopPane.add(newImageFrame);
		newImageFrame.toFront();
                newImageFrame.setTitle("Hough Line Detection");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		try{newImageFrame.setSelected(true);}catch(Exception e){}
            }
	});

        contrastStretchingMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                final JFrame menu = new JFrame();
                menu.setLocation(150, 100);
                menu.setAlwaysOnTop(true);
                menu.setTitle("Automatic Contrast Stretching Menu");
                menu.setSize(650,500);
                menu.setVisible(true);

                menu.setLayout(new GridLayout(1,3));
                JPanel leftPanel = new JPanel(new GridLayout(8,1));
                menu.add(leftPanel);
                JPanel centerPanel = new JPanel(new GridLayout(8,1));
                menu.add(centerPanel);
                JPanel rightPanel = new JPanel(new GridLayout(8,1));
                menu.add(rightPanel);

                JPanel topLeftPanel = new JPanel();
                leftPanel.add(topLeftPanel);
                JPanel topPanel = new JPanel();
                centerPanel.add(topPanel);
                JPanel topRightPanel = new JPanel();
                rightPanel.add(topRightPanel);
                JPanel Red1LeftPanel = new JPanel();
                leftPanel.add(Red1LeftPanel);
                JPanel Red1Panel = new JPanel();
                centerPanel.add(Red1Panel);
                JPanel Red1RightPanel = new JPanel();
                rightPanel.add(Red1RightPanel);
                JPanel Red2LeftPanel = new JPanel();
                leftPanel.add(Red2LeftPanel);
                JPanel Red2Panel = new JPanel();
                centerPanel.add(Red2Panel);
                JPanel Red2RightPanel = new JPanel();
                rightPanel.add(Red2RightPanel);
                JPanel Green1LeftPanel = new JPanel();
                leftPanel.add(Green1LeftPanel);
                JPanel Green1Panel = new JPanel();
                centerPanel.add(Green1Panel);
                JPanel Green1RightPanel = new JPanel();
                rightPanel.add(Green1RightPanel);
                JPanel Green2LeftPanel = new JPanel();
                leftPanel.add(Green2LeftPanel);
                JPanel Green2Panel = new JPanel();
                centerPanel.add(Green2Panel);
                JPanel Green2RightPanel = new JPanel();
                rightPanel.add(Green2RightPanel);
                JPanel Blue1LeftPanel = new JPanel();
                leftPanel.add(Blue1LeftPanel);
                JPanel Blue1Panel = new JPanel();
                centerPanel.add(Blue1Panel);
                JPanel Blue1RightPanel = new JPanel();
                rightPanel.add(Blue1RightPanel);
                JPanel Blue2LeftPanel = new JPanel();
                leftPanel.add(Blue2LeftPanel);
                JPanel Blue2Panel = new JPanel();
                centerPanel.add(Blue2Panel);
                JPanel Blue2RightPanel = new JPanel();
                rightPanel.add(Blue2RightPanel);
                JPanel bottomPanel = new JPanel();
                centerPanel.add(bottomPanel);

                JRadioButton grayButton = new JRadioButton();
                JLabel gray = new JLabel("Grayscale: ");
                JLabel color = new JLabel("Color: ");
                JRadioButton colorButton = new JRadioButton();
                colorButton.setSelected(true);
                ButtonGroup group = new ButtonGroup();
                group.add(grayButton);
                group.add(colorButton);
                topPanel.add(gray);
                topPanel.add(grayButton);
                topPanel.add(color);
                topPanel.add(colorButton);

                JLabel rLowLabel = new JLabel("                                          Red Low");
                Red1LeftPanel.add(rLowLabel);
                JSlider Red1slider = new JSlider();
                Red1slider.setValue(0);
                Red1slider.setMinorTickSpacing(10);
                Red1slider.setMajorTickSpacing(50);
                Red1slider.setPaintTicks(true);
                Red1slider.setPaintLabels(true);
                Red1Panel.add(Red1slider);

                JSlider Red2slider = new JSlider();
                Red2slider.setValue(100);
                Red2slider.setMinorTickSpacing(10);
                Red2slider.setMajorTickSpacing(50);
                Red2slider.setPaintTicks(true);
                Red2slider.setPaintLabels(true);
                Red2Panel.add(Red2slider);
                JLabel rHighLabel = new JLabel("Red High                                            ");
                Red2RightPanel.add(rHighLabel);

                JLabel gLowLabel = new JLabel("                                      Green Low");
                Green1LeftPanel.add(gLowLabel);
                JSlider Green1slider = new JSlider();
                Green1slider.setValue(0);
                Green1slider.setMinorTickSpacing(10);
                Green1slider.setMajorTickSpacing(50);
                Green1slider.setPaintTicks(true);
                Green1slider.setPaintLabels(true);
                Green1Panel.add(Green1slider);

                JSlider Green2slider = new JSlider();
                Green2slider.setValue(100);
                Green2slider.setMinorTickSpacing(10);
                Green2slider.setMajorTickSpacing(50);
                Green2slider.setPaintTicks(true);
                Green2slider.setPaintLabels(true);
                Green2Panel.add(Green2slider);
                JLabel gHighLabel = new JLabel("Green High                                    ");
                Green2RightPanel.add(gHighLabel);

                JLabel bLowLabel = new JLabel("                                       Blue Low");
                Blue1LeftPanel.add(bLowLabel);
                JSlider Blue1slider = new JSlider();
                Blue1slider.setValue(0);
                Blue1slider.setMinorTickSpacing(10);
                Blue1slider.setMajorTickSpacing(50);
                Blue1slider.setPaintTicks(true);
                Blue1slider.setPaintLabels(true);
                Blue1Panel.add(Blue1slider);

                JSlider Blue2slider = new JSlider();
                Blue2slider.setValue(100);
                Blue2slider.setMinorTickSpacing(10);
                Blue2slider.setMajorTickSpacing(50);
                Blue2slider.setPaintTicks(true);
                Blue2slider.setPaintLabels(true);
                Blue2Panel.add(Blue2slider);
                    JLabel bHighLabel = new JLabel("Blue High                                     ");
                Blue2RightPanel.add(bHighLabel);

                JButton run = new JButton("Run");
                bottomPanel.add(run);
                run.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ae){
                        double low = 0.0;
                        double high = 0.0;
                        menu.setVisible(false);
                        ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                        ImageFrame newImageFrame = new ImageFrame(contrastStretching(imageFrame.getImage(), low, high));
                        desktopPane.add(newImageFrame);
                        newImageFrame.toFront();
                        newImageFrame.setTitle("Automatic Contrast Stretching");
                        newImageFrame.addMouseListener(new MouseInputAdapter());
                        newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                        newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                        try{newImageFrame.setSelected(true);}
                        catch(Exception e){}
                    }
                });
            }
        });

        smoothMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame = new ImageFrame(smooth(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
		newImageFrame.toFront();
                newImageFrame.setTitle("Smooth");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
        });

        sharpenMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame = new ImageFrame(sharpen(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("Sharpen");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});

        kMeans.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                final JFrame menu = new JFrame();
                menu.setLocation(150, 100);
                menu.setAlwaysOnTop(true);
                menu.setTitle("Segmentation Menu");
                menu.setSize(370,125);
                menu.setVisible(true);

                menu.setLayout(new GridLayout(3,1));
                JPanel upperPanel = new JPanel();
                menu.add(upperPanel);
                JPanel middlePanel = new JPanel();
                menu.add(middlePanel);
                final JPanel lowerPanel = new JPanel();
                lowerPanel.setVisible(false);
                menu.add(lowerPanel);

                JLabel label = new JLabel("Number of Segments:");
                upperPanel.add(label);
                final JTextField tf = new JTextField(4);
                upperPanel.add(tf);
                JButton button = new JButton("OK");
                upperPanel.add(button);

                JLabel colorLabel = new JLabel("Color Space:");
                middlePanel.add(colorLabel);
                final String[] colorString = {"RGB", "YCbCr", "YUV", "HSB", "XYZ", "LUV"};
                final JComboBox colorBox = new JComboBox(colorString);
                middlePanel.add(colorBox);
                colorBox.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ae){
                        JComboBox cb = (JComboBox)ae.getSource();
                        int index = cb.getSelectedIndex();
                        if(index == 0){
                            menu.setSize(370,135);
                            lowerPanel.setVisible(true);
                        }
                        else{
                            menu.setSize(370,125);
                            lowerPanel.setVisible(false);
                        }
                    }
                });

                JLabel colorSourceLabel = new JLabel("Color Source:");
                lowerPanel.add(colorSourceLabel);
                final JRadioButton rb1 = new JRadioButton("random", true);
                lowerPanel.add(rb1);
                JRadioButton rb2 = new JRadioButton("from image");
                lowerPanel.add(rb2);
                ButtonGroup group3 = new ButtonGroup();
                group3.add(rb1);
                group3.add(rb2);

                button.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ae){
                        boolean colorSource;
                        if (rb1.isSelected())
                            colorSource = true;
                        else
                            colorSource = false;

                        menu.setVisible(false);
                        ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                        ImageFrame newImageFrame = new ImageFrame(kMeansSegmentation(imageFrame.getImage(),
                            Integer.parseInt(tf.getText()), colorBox.getSelectedIndex(), colorSource));
                        desktopPane.add(newImageFrame);
                        newImageFrame.toFront();
                        newImageFrame.setTitle("K-Means: "+Integer.parseInt(tf.getText())+" segments, "+ colorString[colorBox.getSelectedIndex()]);
                        newImageFrame.addMouseListener(new MouseInputAdapter());
                        newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                        newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                        try{newImageFrame.setSelected(true);}catch(Exception e){}
                    }
                });
            }
        });

        fuzzyCMeans.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                final JFrame menu = new JFrame();
                menu.setLocation(150, 100);
                menu.setAlwaysOnTop(true);
                menu.setTitle("Fuzzy C-Means Segmentation Menu");
                menu.setSize(370,158);
                menu.setVisible(true);

                menu.setLayout(new GridLayout(4,1));
                JPanel upperPanel = new JPanel();
                menu.add(upperPanel);
                JPanel middlePanel1 = new JPanel();
                menu.add(middlePanel1);
                JPanel middlePanel2 = new JPanel();
                menu.add(middlePanel2);
                JPanel lowerPanel = new JPanel();
                menu.add(lowerPanel);

                JLabel numSegs = new JLabel("Number of Segments");
                upperPanel.add(numSegs);
                final JTextField c = new JTextField(4);
                c.setText("5");
                upperPanel.add(c);
                JButton run = new JButton("Run");
                upperPanel.add(run);

                JLabel fuzzyLabel = new JLabel("Fuzzyness:");
                middlePanel1.add(fuzzyLabel);
                final JTextField fuzzyTF = new JTextField(4);
                middlePanel1.add(fuzzyTF);
                fuzzyTF.setText("2");
                JLabel colorLabel = new JLabel("Color Space:");
                middlePanel1.add(colorLabel);
                final String[] colorString = {"RGB", "YCbCr", "YUV", "HSB", "XYZ", "LUV"};
                final JComboBox colorBox = new JComboBox(colorString);
                middlePanel1.add(colorBox);

                JLabel stop = new JLabel("Stop Condition: ");
                middlePanel2.add(stop);
                final JRadioButton quality = new JRadioButton("Quality", true);
                middlePanel2.add(quality, true);
                final JTextField qualityTF = new JTextField(4);
                qualityTF.setText("0.001");
                middlePanel2.add(qualityTF);
                JLabel space = new JLabel("                   ");
                middlePanel2.add(space);

                JLabel moreSpace = new JLabel("                              ");
                lowerPanel.add(moreSpace);
                JRadioButton passes = new JRadioButton("Number of Passes");
                lowerPanel.add(passes);
                final JTextField passesTF = new JTextField(4);
                passesTF.setText("10");
                lowerPanel.add(passesTF);

                ButtonGroup bg = new ButtonGroup();
                bg.add(quality);
                bg.add(passes);

                run.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ae){
                        boolean stopCondition;
                        double stopValue;
                        if (quality.isSelected()){
                            stopCondition = true;
                            stopValue = Double.parseDouble(qualityTF.getText());
                        }
                        else{
                            stopCondition = false;
                            stopValue = Double.parseDouble(passesTF.getText());
                        }

                        menu.setVisible(false);
                        ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                        ImageFrame newImageFrame = new ImageFrame(fuzzyCMeansSegmentation(imageFrame.getImage(), Integer.parseInt(c.getText()),
                                Double.parseDouble(fuzzyTF.getText()), colorBox.getSelectedIndex(), stopCondition, stopValue));
                        desktopPane.add(newImageFrame);
                        newImageFrame.toFront();
                        newImageFrame.setTitle("Fuzzy C-Means: "+Integer.parseInt(c.getText())+" segments, "+ colorString[colorBox.getSelectedIndex()]);
                        newImageFrame.addMouseListener(new MouseInputAdapter());
                        newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                        newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                        try{newImageFrame.setSelected(true);}
                        catch(Exception e){}
                    }
                });
            }
        });

        possibilityCMeans.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                final JFrame menu = new JFrame();
                menu.setLocation(150, 100);
                menu.setAlwaysOnTop(true);
                menu.setTitle("Possibility C-Means Segmentation Menu");
                menu.setSize(500,80);
                menu.setVisible(true);

                JPanel panel = new JPanel();
                JLabel numSegs = new JLabel("Number of Segments:");
                panel.add(numSegs);
                final JTextField numSegsTF = new JTextField(4);
                numSegsTF.setText("5");
                panel.add(numSegsTF);
                JLabel fuzzyness = new JLabel("Fuzzyness:");
                panel.add(fuzzyness);
                final JTextField fuzzynessTF = new JTextField(4);
                fuzzynessTF.setText("2");
                panel.add(fuzzynessTF);
                JLabel epsilon = new JLabel("epsilon:");
                panel.add(epsilon);
                final JTextField epsilonTF = new JTextField(4);
                epsilonTF.setText("0.0001");
                panel.add(epsilonTF);
                JButton run = new JButton("Run");
                panel.add(run);
                run.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ae){
                        int segs = Integer.parseInt(numSegsTF.getText());
                        double fuzzy = Double.parseDouble(fuzzynessTF.getText());
                        double ep = Double.parseDouble(epsilonTF.getText());
                        menu.setVisible(false);
                        ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                        ImageFrame newImageFrame = new ImageFrame(possibilityCMeansSegmentation(imageFrame.getImage(),segs,fuzzy,ep));
                        desktopPane.add(newImageFrame);
                        newImageFrame.toFront();
                        newImageFrame.setTitle("Possibility C-Means Segmentation");
                        newImageFrame.addMouseListener(new MouseInputAdapter());
                        newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                        newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                        try{newImageFrame.setSelected(true);}catch(Exception e){}
                    }
                });
                menu.add(panel);
            }
        });

        transformationsMenu.add(ainetTransformationsMenuItem);
        transformationsMenu.add(rotateSubmenu);
        transformationsMenu.add(grayscaleTransformationsMenuItem);
        transformationsMenu.add(histogramTransformationMenuItem);
        transformationsMenu.add(contrastStretchingMenuItem);
        transformationsMenu.add(segmentationMenu);
        transformationsMenu.add(edgeDetectMenu);
        transformationsMenu.add(lineDetectMenu);
        transformationsMenu.add(smoothMenuItem);
        transformationsMenu.add(sharpenMenuItem);
        transformationsMenu.addSeparator();
        transformationsMenu.add(customTransformationsMenuItem);
        menuBar.add(transformationsMenu);

        JMenu filtersMenu = new JMenu("Filter");
        JMenuItem contrastEnhancementFiltersMenuItem = new JMenuItem("Contrast Enhancement");
	JMenuItem customFiltersMenuItem = new JMenuItem("Custom...");
        contrastEnhancementFiltersMenuItem.setEnabled(false);
	customFiltersMenuItem.setEnabled(false);
        filtersMenu.add(contrastEnhancementFiltersMenuItem);
	filtersMenu.addSeparator();
	filtersMenu.add(customFiltersMenuItem);
        menuBar.add(filtersMenu);

	setJMenuBar(menuBar);
        //////////This is my part.
        JMenu My_transformationsMenu = new JMenu("My_Transformations");
        JMenuItem My_contrastStretchingMenuItem = new JMenuItem("Automated Contrast Stretching");
        JMenuItem My_SobelOperatorMenuItem = new JMenuItem("Sober Operator");
        JMenuItem My_histogramMenueItem = new JMenuItem("My_Histgrom");
        JMenuItem My_KmeansSegmentItem = new JMenuItem("K-means Segmentation");
        JMenuItem My_FuzzyCmeansItem = new JMenuItem("Fuzzy C-means Segmentation");
        JMenuItem My_OptimalThresholdItem = new JMenuItem("Optimal Thresholding");
        JMenu My_GrayScaleMorphologyMenu = new JMenu("Morphological Operation");
        JMenuItem My_MorphologyItemErosion = new JMenuItem("Erosion");
        JMenuItem My_MorphologyItemDilation = new JMenuItem("Dilation");
        JMenuItem My_MorphologyItemOpen = new JMenuItem("Opening");
        JMenuItem My_MorphologyItemClose = new JMenuItem("Closing");
        JMenuItem RGBtoHSIItem = new JMenuItem("RGB to HSI");
        JMenuItem HSIItemtoRGB = new JMenuItem("HSI to RGB ");
        JMenuItem Binarythinnig = new JMenuItem("Binary Thinning");
        JMenuItem CornerItem = new JMenuItem(" Corner Detection");



       CornerItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame = new ImageFrame(MycornerDetection(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("Corner Detection");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});

        Binarythinnig.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame = new ImageFrame(doThinning(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("Binary Thinning");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});

       HSIItemtoRGB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame = new ImageFrame(MyHSItoRGB(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("HSI to RGB");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});

        RGBtoHSIItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame = new ImageFrame(MyRGBtoHSI_H(imageFrame.getImage()));
                ImageFrame newImageFrame1 = new ImageFrame(MyRGBtoHSI_H(imageFrame.getImage()));
                ImageFrame newImageFrame2 = new ImageFrame(MyRGBtoHSI_S(imageFrame.getImage()));
                ImageFrame newImageFrame3 = new ImageFrame(MyRGBtoHSI_I(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
                desktopPane.add(newImageFrame1);
                desktopPane.add(newImageFrame2);
                desktopPane.add(newImageFrame3);
                newImageFrame.toFront();
                newImageFrame1.toFront();
                newImageFrame2.toFront();
                newImageFrame3.toFront();
                newImageFrame.setTitle("RGB to HSI");
                newImageFrame1.setTitle("HSI Image H");
                newImageFrame2.setTitle("HSI Image S");
                newImageFrame3.setTitle("HSI Image I");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});
        My_MorphologyItemClose.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame1 = new ImageFrame(colorToGrayscale(imageFrame.getImage()));
                ImageFrame newImageFrame = new ImageFrame(MyMorphologyClose(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
                desktopPane.add(newImageFrame1);
                newImageFrame1.toFront();
                newImageFrame.toFront();
                newImageFrame1.setTitle("Color to Gray");
                newImageFrame.setTitle("Closing (Gray Level)");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});

        My_MorphologyItemOpen.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame1 = new ImageFrame(colorToGrayscale(imageFrame.getImage()));
                ImageFrame newImageFrame = new ImageFrame(MyMorphologyOpen(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
                desktopPane.add(newImageFrame1);
                newImageFrame1.toFront();
                newImageFrame.toFront();
                newImageFrame1.setTitle("Color to Gray");
                newImageFrame.setTitle("Opening (Gray Level)");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});
       My_MorphologyItemErosion.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame1 = new ImageFrame(colorToGrayscale(imageFrame.getImage()));
                ImageFrame newImageFrame = new ImageFrame(MyMorphologyErosion(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
                desktopPane.add(newImageFrame1);
                newImageFrame1.toFront();
                newImageFrame.toFront();
                newImageFrame1.setTitle("Color to Gray");
                newImageFrame.setTitle("Erosion (Gray Level)");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});
        My_MorphologyItemDilation.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame1 = new ImageFrame(colorToGrayscale(imageFrame.getImage()));
                ImageFrame newImageFrame = new ImageFrame(MyMorphologyDilation(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
                desktopPane.add(newImageFrame1);
                newImageFrame1.toFront();
                newImageFrame.toFront();
                newImageFrame1.setTitle("Color to Gray");
                newImageFrame.setTitle("Dilation (Gray Level)");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});
        My_OptimalThresholdItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();

                ImageFrame newImageFrame = new ImageFrame(MyOptimalthredshold(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("Optimal Thresholding");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});

               My_KmeansSegmentItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                String inputK = JOptionPane.showInputDialog(null,
                        "Please input K:", "K-means Segmentation", JOptionPane.QUESTION_MESSAGE);
                int k = Integer.parseInt(inputK);
                //ImageFrame newImageFrame = new ImageFrame(kMeansSegmentation(imageFrame.getImage(),
                  //          Integer.parseInt(tf.getText()));
                ImageFrame newImageFrame = new ImageFrame(MyKmeansSegmentation(imageFrame.getImage(),k));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("K-means Segmentation(K = "+k +" )");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});
       My_FuzzyCmeansItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                String inputK = JOptionPane.showInputDialog(null,
                        "Please input C:", "Fuzzy C-means Segmentation", JOptionPane.QUESTION_MESSAGE);
                int C = Integer.parseInt(inputK);
                ImageFrame newImageFrame = new ImageFrame(MyFuzzyCmeansSeg(imageFrame.getImage(),C));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("Fuzzy C-means Segmentation(C = "+C +" ,m = 2,e = 0)");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});
        My_contrastStretchingMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame1 = new ImageFrame(colorToGrayscale(imageFrame.getImage()));
                ImageFrame newImageFrame = new ImageFrame(My_contrastStretch(imageFrame.getImage()));
                desktopPane.add(newImageFrame1);
                desktopPane.add(newImageFrame);
                newImageFrame1.toFront();
                newImageFrame1.setTitle("Color to Gray");
                newImageFrame.toFront();
                newImageFrame.setTitle("My_automatedContraststreching");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});
         My_SobelOperatorMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame1 = new ImageFrame(GradientSobel(imageFrame.getImage()));
                ImageFrame newImageFrame = new ImageFrame(OrientationSobel(imageFrame.getImage()));
                desktopPane.add(newImageFrame1);
                desktopPane.add(newImageFrame);
                newImageFrame1.toFront();
                newImageFrame1.setTitle("Gradient image by Sobel");
                newImageFrame.toFront();
                newImageFrame.setTitle("Orientation edge by Sobel");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                newImageFrame1.addMouseListener(new MouseInputAdapter());
                newImageFrame1.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame1.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
                try{newImageFrame1.setSelected(true);}
                catch(Exception e){}
            }
	});
         My_histogramMenueItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                ImageFrame newImageFrame = new ImageFrame(My_histogram(imageFrame.getImage()));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("  Histogram Equalization");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});

        My_GrayScaleMorphologyMenu.add(My_MorphologyItemErosion);
        My_GrayScaleMorphologyMenu.add(My_MorphologyItemDilation);
        My_GrayScaleMorphologyMenu.add(My_MorphologyItemOpen);
        My_GrayScaleMorphologyMenu.add(My_MorphologyItemClose);

        My_transformationsMenu.add(CornerItem);
        My_transformationsMenu.add(Binarythinnig);
        My_transformationsMenu.add(HSIItemtoRGB);
        My_transformationsMenu.add(RGBtoHSIItem);
        My_transformationsMenu.add(My_FuzzyCmeansItem);
        My_transformationsMenu.add(My_KmeansSegmentItem);
        My_transformationsMenu.add(My_contrastStretchingMenuItem);
        My_transformationsMenu.add(My_SobelOperatorMenuItem);
        My_transformationsMenu.add(My_histogramMenueItem);
        My_transformationsMenu.add(My_OptimalThresholdItem);
        My_transformationsMenu.add(My_GrayScaleMorphologyMenu);
        menuBar.add(My_transformationsMenu);
      //  menuBar.add(My_SobelOperatorMenuItem);

                       ///////////////////////////////////////////////////////////////
        JMenu HyspectralDisMenu = new JMenu("Hyspectral Discramnation");
        JMenuItem SepctralAMapperItem = new JMenuItem("Spectral Angle Mapper");
        JMenuItem SpectralInformaItem = new JMenuItem("Sepctral Information Divergence");
        JMenuItem SuperKmeansItem = new JMenuItem("(SAM)Super K means Segmentaion");
        JMenuItem SuperKmeans2Item = new JMenuItem("(SIM)Supper K means Segmentation");
   //     JMenuItem SuperKmeans3Item = new JMenuItem("(Entropy)Supper K means Segmentation");

        //////SIM
        SuperKmeans2Item.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                String inputK = JOptionPane.showInputDialog(null,
                        "Please input K:", "K-means Segmentation", JOptionPane.QUESTION_MESSAGE);
                int k = Integer.parseInt(inputK);
                ImageFrame newImageFrame = new ImageFrame(MySuperkMeansSIMSegmetation(imageFrame.getImage(), k));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("SIM (K = "+k +" )");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});
        /////SAM
        SuperKmeansItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                String inputK = JOptionPane.showInputDialog(null,
                        "Please input K:", "K-means Segmentation", JOptionPane.QUESTION_MESSAGE);
                int k = Integer.parseInt(inputK);
                ImageFrame newImageFrame = new ImageFrame(My_SAMSuperkMeansSegmetation(imageFrame.getImage(), k));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("Super K-means Segmentation (K = "+k +" )");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});

        SpectralInformaItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                String inputK = JOptionPane.showInputDialog(null,
                        "Please input K:", "K-means Segmentation", JOptionPane.QUESTION_MESSAGE);
                int k = Integer.parseInt(inputK);
                ImageFrame newImageFrame = new ImageFrame(MySpectralInformationDivergence(imageFrame.getImage(), k));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("Spectral Information Divergence (K = "+k +" )");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});
        SepctralAMapperItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                String inputK = JOptionPane.showInputDialog(null,
                        "Please input K:", "K-means Segmentation", JOptionPane.QUESTION_MESSAGE);
                int k = Integer.parseInt(inputK);
                //ImageFrame newImageFrame = new ImageFrame(kMeansSegmentation(imageFrame.getImage(),
                //          Integer.parseInt(tf.getText()));
                ImageFrame newImageFrame = new ImageFrame(MySpectralAngleMapper(imageFrame.getImage(), k));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("Spectral Angle Mapper (K = "+k +" )");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});
      //  HyspectralDisMenu.add(SuperKmeans5Item);
      //  HyspectralDisMenu.add(SuperKmeans4Item);
      //  HyspectralDisMenu.add(SuperKmeans3Item);
        HyspectralDisMenu.add(SuperKmeans2Item);
        HyspectralDisMenu.add(SuperKmeansItem);
        HyspectralDisMenu.add(SpectralInformaItem);
        HyspectralDisMenu.add(SepctralAMapperItem);
        menuBar.add(HyspectralDisMenu);

        JMenu ACOMenu = new JMenu("Ant Colony Optimization");
        JMenuItem ACOMenuItem = new JMenuItem("Anto Colony Optimization");
        JMenuItem RFCMenuItem = new JMenuItem("Robust Fuzzy C-means");

        ACOMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                String inputK = JOptionPane.showInputDialog(null,
                        "Please input K:", "K-means Segmentation", JOptionPane.QUESTION_MESSAGE);
                int k = Integer.parseInt(inputK);

                ImageFrame newImageFrame = new ImageFrame(MyACO(imageFrame.getImage(), k));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("Ant Colony Optimization (K = "+k +" )");
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});
        RFCMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                ImageFrame imageFrame = (ImageFrame) desktopPane.getSelectedFrame();
                String inputK = JOptionPane.showInputDialog(null,
                        "Please input K:", "K-means Segmentation", JOptionPane.QUESTION_MESSAGE);
                int k = Integer.parseInt(inputK);
                String inputF = JOptionPane.showInputDialog(null,
                        "Please input Fuzzness:", "K-means Segmentation", JOptionPane.QUESTION_MESSAGE);
                double f = Double.parseDouble(inputF);

                ImageFrame newImageFrame = new ImageFrame(MyRobustFuzzyCmeansSeg(imageFrame.getImage(), k , f));
                desktopPane.add(newImageFrame);
                newImageFrame.toFront();
                newImageFrame.setTitle("Robust Fuzzy C-means (K = "+k +" f: " + f + " )" );
                newImageFrame.addMouseListener(new MouseInputAdapter());
                newImageFrame.addMouseMotionListener(new MouseInputAdapter());
                newImageFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                try{newImageFrame.setSelected(true);}
                catch(Exception e){}
            }
	});
        ACOMenu.add(RFCMenuItem);
        ACOMenu.add(ACOMenuItem);
        menuBar.add(ACOMenu);

        JPanel bottomBar = new JPanel();
        MouseInputAdapter.red = new JTextField(4);
        MouseInputAdapter.red.setEditable(false);
        bottomBar.add(MouseInputAdapter.red);
        MouseInputAdapter.green = new JTextField(4);
        MouseInputAdapter.green.setEditable(false);
        bottomBar.add(MouseInputAdapter.green);
        MouseInputAdapter.blue = new JTextField(4);
        MouseInputAdapter.blue.setEditable(false);
        bottomBar.add(MouseInputAdapter.blue);
        contentPane.add(bottomBar, BorderLayout.SOUTH);


	addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent we){
                System.exit(0);
            }
	});
        setSize(1000, 800);
        show();
    }
    protected int[][][] pixelsArrayToTRGBArray(int[] pixels, Dimension imageInDimension)
	{

		// Declare local storage
		int imagePixelLength = (int) (imageInDimension.getWidth() * imageInDimension.getHeight());
		int TRGB[][][] = new int[4][(int) imageInDimension.getWidth()][(int) imageInDimension.getHeight()];

		// Convert pixel array to TRGB array
		for (int column = 0, row = 0, pixelIndex = 0; pixelIndex < imagePixelLength; pixelIndex++)
		{

			// Store transparency
			TRGB[0][column][row] = getTransparencyComponent(pixels[pixelIndex]);

			// Store red
			TRGB[1][column][row] = getRedComponent(pixels[pixelIndex]);

			// Store green
			TRGB[2][column][row] = getGreenComponent(pixels[pixelIndex]);

			// Store blue
			TRGB[3][column][row] = getBlueComponent(pixels[pixelIndex]);

			// Calculate column and row indexes
			if (++column == imageInDimension.getWidth())
			{
				// Reset column and increment row
				column = 0;
				row++;
			}

		}

		// Return the newly generated TRGB array
		return TRGB;

	}
  private static final int NDimention=3;
    //bird training = 213
    //scene training = 3400
    //ground training = 6425
    private static final int Training_AgScale=6425;
    //bird testing = 20584
    //scene testing = 18369
    //ground testing = 262144
    private static final int AgScale=262144;
    private static final int max_iter = 500;
    private static int MaxValue=255;
    private static final int BaseScale=500;
    private static final int diversityCount=BaseScale*3;
    private static final int Clonal_BaseScale=BaseScale*3;
    private static final int Initial_AbScale=1000;
    private static double supression_threshold = 0.09;
    private static double metadynamics_threshold = 0.7;
   // private static double upperbound = 3.0;
    //private static double lowerbound = 1.0;


     /*private  Antibody Initial_Ab[]=new Antibody[Initial_AbScale];
     private  Antibody AbBase[]=new Antibody[BaseScale];
     //private  Antigen Whole_Ag[]=new Antigen[AgScale];
     private  Antigen Training_Ag[]=new Antigen[Training_AgScale];
     private  ArrayList<Antibody> Reconstructed_Antibody_Pool = new ArrayList<Antibody>(BaseScale+Clonal_BaseScale+diversityCount);
     private  ArrayList<Antibody> final_Reconstructed_Antibody_Pool = new ArrayList<Antibody>(BaseScale+Clonal_BaseScale+diversityCount);
     private  ArrayList<Antibody> clonal_population = new ArrayList<Antibody>(Clonal_BaseScale);
     private  ArrayList<Antibody> final_clonal_population = new ArrayList<Antibody>(Clonal_BaseScale);
*/

     public static class Antigen
    {

        public double AgValue[]=new  double[NDimention];
        public int AgClass=0;
        public void setValue(int i, double f)
        {
            this.AgValue[i]=f;
        }
    }
    public static class Antibody
    {

        private double AbValue[]=new double[NDimention];
         public Antibody()
        {
        for(int i=0;i<NDimention;i++)
        this.AbValue[i]=0;
        }
        private double Affinity=0;
        private int AbClass=0;
        private Antigen Ag = new Antigen();


        public void setValue(int i, double f)
        {
            this.AbValue[i]=f;
        }

        public void setAntigen(Antigen Ag)
        {
            this.Ag=Ag;
        }

    }

    //To calculate the affinity between given Antibody and Antigen
    public static double getAffinity(Antigen Ag, Antibody Ab)
    {
        double EuclidianDistance = 0;
        for(int i=0;i<NDimention;i++)
            if(Ag!=null&&Ab!=null)
            //EuclidianDistance=(double)EuclidianDistance+((Ag.AgValue[i]-Ab.AbValue[i])*(Ag.AgValue[i]-Ab.AbValue[i]));

        //return (double)(1/1+Math.sqrt(EuclidianDistance));
                EuclidianDistance = EuclidianDistance+Math.abs(Ag.AgValue[i] - Ab.AbValue[i]);
        return (1/EuclidianDistance);
    }

    //To calculate the affinity between two antibodies
    public static double getAffinity(Antibody Ab1, Antibody Ab2)
    {
        double EuclidianDistance = 0;
        for(int i=0;i<NDimention;i++)
            if(Ab1!=null&&Ab2!=null)
           //  EuclidianDistance=(double)EuclidianDistance+((Ab1.AbValue[i]-Ab2.AbValue[i])*(Ab1.AbValue[i]-Ab2.AbValue[i]));
       // return (double)(1/1+Math.sqrt(EuclidianDistance));
                EuclidianDistance = EuclidianDistance+Math.abs(Ab1.AbValue[i] - Ab2.AbValue[i]);
        return (1/EuclidianDistance);
    }



//Manual assignment of one Antibody to another Antibody
     public static void equate(Antibody Ab1,Antibody Ab2)
    {
         if(Ab1!=null&&Ab2!=null)
         {
        for(int i=0;i<NDimention;i++)
          Ab1.AbValue[i]=Ab2.AbValue[i];

        Ab1.AbClass=Ab2.AbClass;
        Ab1.Affinity = Ab2.Affinity;
        Ab1.Ag = Ab2.Ag;
        }
    }

//Used to find the overall correctness
    public static double Whole_Affinity(Antibody []Ab,Antigen []Ag,int AgScale)
    {
        int correct=0;
        Antibody ab = null;
        ab=new Antibody();
       // equle(ab,Ab[0]);
        for(int i=0;i<AgScale;i++)
        {
            for(int j=0;j<BaseScale;j++)
                if(j==0||(getAffinity(Ag[i],ab)<getAffinity(Ag[i],Ab[j])))
                    equate(ab,Ab[j]);
            if(Ag[i]!=null&&ab!=null)
            if(Ag[i].AgClass==ab.AbClass)
                correct++;
        }
        return (double)correct/(double)AgScale;
    }

//Generate clone_population
    public void Clonal_Expansion(Antibody[] AbBase,ArrayList<Antibody> clonal_population,Antigen[] Training_Ag){

        int clone_count = 0, i = 0,j = 0,k = 0;
        Random rand = new Random();
        Antibody ab = null;

        //Generate clones for each antibody
        for(i = 0;i < BaseScale; i++)
        {
            if((AbBase[i].Affinity/2) > rand.nextDouble())
                clone_count = 3;
            else
                clone_count = rand.nextInt(2) + 1;
       // clone_count = 1;//rand.nextInt(3)+1;
            for(j = 0;j < clone_count; j++){
                ab = new Antibody();
              /*  for(k = 0;k<NDimention;k++){
                if(AbBase[i].Affinity/2>rand.nextDouble())
                ab.AbValue[k]=(double) (AbBase[i].AbValue[k]);
                else
                ab.AbValue[k]=rand.nextDouble()*MaxValue;
                }*/
                equate(ab,AbBase[i]);
                clonal_population.add(ab);
                }
            }

        for(j=0;j<clonal_population.size();j++)
                  {
                    for(i=0;i<Training_AgScale;i++)
                     {
                        if(clonal_population.get(j)!=null&&Training_Ag[i]!=null)
                        if(i==0||(clonal_population.get(j).Affinity<getAffinity(Training_Ag[i],clonal_population.get(j))))
                        {
                            if(clonal_population.get(j)!=null&&Training_Ag[i]!=null)
                            {
                            clonal_population.get(j).Affinity = getAffinity(Training_Ag[i],clonal_population.get(j));
                            clonal_population.get(j).AbClass=Training_Ag[i].AgClass;
                            clonal_population.get(j).Ag=Training_Ag[i];
                            }
                        }
                     }
                  }
     /*   System.out.print("\nclonal expansion :\n");
        for(i = 0;i < clonal_population.size(); i++)
            System.out.println(clonal_population.get(i).AbValue[0]+" "+clonal_population.get(i).AbValue[1]+" "+clonal_population.get(i).AbValue[2]+" "+clonal_population.get(i).AbValue[3]+" class "+clonal_population.get(i).AbClass);
      *
      */
    }

    //Mutate the clone population inversly proportional to affinity
    public static void Affinity_Maturation(ArrayList<Antibody> clonal_population,Antibody[] AbBase,Antigen[] Training_Ag, double correctness){

        int total_clone_count = clonal_population.size();
        for(int i = 0;i < total_clone_count;i++){
            float alpha = (float)(1/clonal_population.get(i).Affinity);
            for(int j = 0;j<NDimention;j++){
                clonal_population.get(i).AbValue[j] = clonal_population.get(i).AbValue[j] +
                     alpha;//*(clonal_population.get(i).Ag.AgValue[j]-clonal_population.get(i).AbValue[j]);
            }
        }




        //After affinity maturation recalculate the affinity of clonal population and the class it belongs to
        for(int j=0;j<clonal_population.size();j++)
                  {
                    for(int i=0;i<Training_AgScale;i++)
                     {
                        if(clonal_population.get(j)!=null&&Training_Ag[i]!=null)
                        if(i==0||(clonal_population.get(j).Affinity<getAffinity(Training_Ag[i],clonal_population.get(j))))
                        {
                            if(clonal_population.get(j)!=null&&Training_Ag[i]!=null)
                            {
                            clonal_population.get(j).Affinity = getAffinity(Training_Ag[i],clonal_population.get(j));
                            clonal_population.get(j).AbClass=Training_Ag[i].AgClass;
                            clonal_population.get(j).Ag=Training_Ag[i];
                            }
                        }
                     }
                  }

       /* System.out.println("\nafter affinity maturation :\n" );
        for(int i = 0;i < clonal_population.size(); i++)
            System.out.println(clonal_population.get(i).AbValue[0]+" "+clonal_population.get(i).AbValue[1]+" "+clonal_population.get(i).AbValue[2]+" "+clonal_population.get(i).AbValue[3]+" class"+clonal_population.get(i).AbClass);
*/
       /* Antibody ab = new Antibody();
        for(int j = 0;j<clonal_population.size();j++){
            for(int k = 0;k<BaseScale;k++){
                if(clonal_population.get(j).Affinity == AbBase[k].Affinity){
                    clonal_population.remove(j);
                    if(j != 0)
                    j--;
                }
                else if(clonal_population.get(j).Affinity > AbBase[k].Affinity){
                    equate(ab,AbBase[k]);
                    equate(AbBase[k],clonal_population.get(j));

                    if(Whole_Affinity(AbBase,Training_Ag,Training_AgScale)<correctness)
                             equate(AbBase[k],ab);
                }
            }
        }*/


    }

    //Remove those clones whose affinity is less than the natural threshold
    public static void Metadynamics(ArrayList<Antibody> clonal_population){


        for(int i=0;i<clonal_population.size();i++){
            if ((1/clonal_population.get(i).Affinity) > metadynamics_threshold){
                clonal_population.remove(i);
                i--;
            }
        }
         /*   System.out.println("\n after metadynamics :\n" );
        for(int i = 0;i < clonal_population.size(); i++)
            System.out.println(clonal_population.get(i).AbValue[0]+" "+clonal_population.get(i).AbValue[1]+" "+clonal_population.get(i).AbValue[2]+" "+clonal_population.get(i).AbValue[3]+" class"+clonal_population.get(i).AbClass);
        */
    }


    //Remove those clones whose affinity with each other is less than the supression threshold
    public static void Clonal_Supression(ArrayList<Antibody> clonal_population,ArrayList<Antibody> final_clonal_population){
        int i = 0,j = 0;
        int size = clonal_population.size();
        boolean flag = false;
        for(i=0;i<size;i++){
            for(j=i+1;j<size;j++){
                if(clonal_population.get(i)!= null && clonal_population.get(j)!= null)
                if(getAffinity(clonal_population.get(i), clonal_population.get(j)) < supression_threshold)
                {
                   // flag = true;
                    break;
                }
            }//inner for loop
            if(j == size)
                final_clonal_population.add(clonal_population.get(i));
        }//outer for loop

    }


//concatenate the antibody base with the final clonal population
    public static void Network_Reconstruction(ArrayList<Antibody> Reconstructed_Antibody_Pool,ArrayList<Antibody> final_clonal_population,Antibody[] AbBase){

        int k = 0;
       for(int i = 0;i<BaseScale;i++)
           Reconstructed_Antibody_Pool.add(AbBase[i]);

       for(int j = 0;j<final_clonal_population.size();j++){
           Reconstructed_Antibody_Pool.add(final_clonal_population.get(j));

        }
    }

 //network supression
    public static void Network_Interaction_Supression(ArrayList<Antibody> Reconstructed_Antibody_Pool,ArrayList<Antibody> final_Reconstructed_Antibody_Pool){
      int i = 0,j = 0;
      boolean flag = false;
            int size = Reconstructed_Antibody_Pool.size();
            for(i=0;i<size;i++){
                for(j=i+1;j<size;j++){
                    if(Reconstructed_Antibody_Pool.get(i)!= null && Reconstructed_Antibody_Pool.get(j)!= null)
                    if(getAffinity(Reconstructed_Antibody_Pool.get(i), Reconstructed_Antibody_Pool.get(j)) < supression_threshold)
                    {
                        //flag = true;
                        break;
                    }
                }//inner for loop
                if(j == size)
                    final_Reconstructed_Antibody_Pool.add(Reconstructed_Antibody_Pool.get(i));
            }//outer for loop

    }


    //Introduce diversity to continue the while loop
    public static void Introduce_Diversity(ArrayList<Antibody> final_Reconstructed_Antibody_Pool,Antigen[] Training_Ag){

        Random rand  = new Random();
        Antibody Ab ;
        int size = final_Reconstructed_Antibody_Pool.size();
        for(int i = 0;i<diversityCount;i++)
        {
            Ab = new Antibody();
            for (int j = 0; j < NDimention; j++)
            {
               Ab.setValue(j,MaxValue * rand.nextDouble());
            }

            final_Reconstructed_Antibody_Pool.add(Ab);
        }

        for(int j=0;j<final_Reconstructed_Antibody_Pool.size();j++)
                  {
                    for(int i=0;i<Training_AgScale;i++)
                     {
                        if(final_Reconstructed_Antibody_Pool.get(j)!=null&&Training_Ag[i]!=null)
                        if(i==0||(final_Reconstructed_Antibody_Pool.get(j).Affinity<getAffinity(Training_Ag[i],final_Reconstructed_Antibody_Pool.get(j))))
                        {
                            if(final_Reconstructed_Antibody_Pool.get(j)!=null&&Training_Ag[i]!=null)
                            {
                            final_Reconstructed_Antibody_Pool.get(j).Affinity = getAffinity(Training_Ag[i],final_Reconstructed_Antibody_Pool.get(j));
                            final_Reconstructed_Antibody_Pool.get(j).AbClass=Training_Ag[i].AgClass;
                            final_Reconstructed_Antibody_Pool.get(j).Ag=Training_Ag[i];
                            }
                        }
                     }
                  }
    }



    public void ainet(Antigen Whole_Ag[]) throws IOException {

        Antibody Initial_Ab[]=new Antibody[Initial_AbScale];
     Antibody AbBase[]=new Antibody[BaseScale];
     //private  Antigen Whole_Ag[]=new Antigen[AgScale];
      Antigen Training_Ag[]=new Antigen[Training_AgScale];
      ArrayList<Antibody> Reconstructed_Antibody_Pool = new ArrayList<Antibody>(BaseScale+Clonal_BaseScale+diversityCount);
      ArrayList<Antibody> final_Reconstructed_Antibody_Pool = new ArrayList<Antibody>(BaseScale+Clonal_BaseScale+diversityCount);
      ArrayList<Antibody> clonal_population = new ArrayList<Antibody>(Clonal_BaseScale);
      ArrayList<Antibody> final_clonal_population = new ArrayList<Antibody>(Clonal_BaseScale);
     //   initialize(Initial_Ab,AbBase ,Whole_Ag,Training_Ag);
      ////////////////////////////////////////////////////////////////////////////////////////////////////////

      //randomly generate all the antibodies
        Random random=new Random();

        for(int i=0;i<Initial_AbScale;i++)
        { Initial_Ab[i]=new Antibody();
            for(int j=0;j<NDimention;j++)
            {
             // random=new Random();
                if(Initial_Ab[i]!=null)
               Initial_Ab[i].setValue(j,random.nextDouble()*MaxValue);
            }
        }

//initialise the whole antigen community
        FileReader fin;
        Scanner src;
        try{
        fin=new FileReader("ground.txt");

        src=new Scanner(fin);

        for(int i=0;i<AgScale;i++)
        {
            if(src.hasNext())
            Whole_Ag[i]=new Antigen();
            for(int j=0;j<NDimention;j++)
         {

             if(Whole_Ag[i]!=null)
             Whole_Ag[i].AgValue[j]=src.nextDouble();
         }

        }

        }catch(IOException e){
            System.out.print(e);
        }

//Initialise the training antigen
        try{
        fin=new FileReader("ground_training.txt");
        src=new Scanner(fin);

        for(int i=0;i<Training_AgScale;i++)
        {
            Training_Ag[i]=new Antigen();
            for(int j=0;j<NDimention;j++)
         {

             if(Training_Ag[i]!=null)
             Training_Ag[i].AgValue[j]=src.nextDouble();
         }
         if(src.hasNextInt())
        Training_Ag[i].AgClass=src.nextInt();
        }
        }catch(IOException e){
            System.out.print(e);
        }


//For each antibody find the highest affinity with any antigen and the class it belongs to
         for(int j=0;j<Initial_AbScale;j++)
          {
            for(int i=0;i<Training_AgScale;i++)
             {
                if(Initial_Ab[j]!=null&&Training_Ag[i]!=null)
                if(i==0||(Initial_Ab[j].Affinity<getAffinity(Training_Ag[i],Initial_Ab[j])))
                {
                    if(Initial_Ab[j]!=null&&Training_Ag[i]!=null)
                    {
                    Initial_Ab[j].Affinity = getAffinity(Training_Ag[i],Initial_Ab[j]);
                    Initial_Ab[j].AbClass=Training_Ag[i].AgClass;
                    Initial_Ab[j].Ag=Training_Ag[i];
                    }
                }
             }
         }

//Take the top 'AbScale' number of antibodies with highest affinity

         int highest = 0;
              for(int i=0;i<BaseScale;i++)
              {

                 AbBase[i]=new Antibody();
                      equate(AbBase[i],Initial_Ab[i]);

              }

                  for(int j=0;j<BaseScale;j++)
                  {
                      for(int i=BaseScale;i<Initial_AbScale;i++)
                      if((AbBase[j].Affinity<Initial_Ab[i].Affinity))
                      {
                          equate(AbBase[j],Initial_Ab[i]);
                          highest = i;
                      }
                  Initial_Ab[highest].Affinity=0;
                  }


      //////////////////////////////////////////////////////////////////////////////////////////////////////////
         int iter_count = 0;
         double correctness_current_iteration = 0.0;
        // double correctness_previous_iteration = 0.0;

         double[] y=new double[1000];
                        int m=0;
         while(true){
            
                 correctness_current_iteration = Whole_Affinity(AbBase,Training_Ag,Training_AgScale);
            //    System.out.println("after iteration "+ iter_count+" whole affinity is "+correctness_current_iteration);
              if(correctness_current_iteration > 0.99 || m >=20 || iter_count > max_iter){
                  //System.out.print("Breaking while loop after "+iter_count+" times");
                  break;
             }
              else
              {
                      iter_count++;
                  Clonal_Expansion(AbBase,clonal_population,Training_Ag);

                  Affinity_Maturation(clonal_population,AbBase,Training_Ag,correctness_current_iteration);





                  Metadynamics(clonal_population);
                  Clonal_Supression(clonal_population,final_clonal_population);
                  /////////////////////////////////////////////////////////////////////////////////////////


                  ///////////////////////////////////////////////////////////////////////////////////////////
                  Network_Reconstruction(Reconstructed_Antibody_Pool, final_clonal_population, AbBase);
                  Network_Interaction_Supression(Reconstructed_Antibody_Pool ,final_Reconstructed_Antibody_Pool);
                  Introduce_Diversity(final_Reconstructed_Antibody_Pool,Training_Ag);


//Find the top 'baseScale' antibodies from the final reconstructed antibody pool and repeate the loop

              int c=0;
              for(int i=0;i<BaseScale;i++)
              {

                 AbBase[i]=new Antibody();
                      equate(AbBase[i],final_Reconstructed_Antibody_Pool.get(i));

              }

                  for(int j=0;j<10;j++)
                  {
                      for(int i=10;i<final_Reconstructed_Antibody_Pool.size();i++)
                      if((AbBase[j].Affinity<final_Reconstructed_Antibody_Pool.get(i).Affinity))
                      {
                          equate(AbBase[j],final_Reconstructed_Antibody_Pool.get(i));
                          c=i;
                      }
                  final_Reconstructed_Antibody_Pool.get(c).Affinity = 0;
                  }

              }//end of else

            /*     System.out.println(Whole_Affinity(AbBase,Training_Ag,Training_AgScale));
                                System.out.println(iter_count);
                                int n=0;
                                for(n=0;n<m;n++)
                                {
                                    if(y[n]==Whole_Affinity(AbBase,Training_Ag,Training_AgScale))
                                        break;
                                }
                                if(n<m||m==0)
                                { y[m]=Whole_Affinity(AbBase,Training_Ag,Training_AgScale);
                                m++;}
                                else if(n==m)
                                {
                                    y[0]=Whole_Affinity(AbBase,Training_Ag,Training_AgScale);
                                    m=0;
                                } */




         }//end of while

         for(int i=0;i<BaseScale;i++)
            System.out.println(AbBase[i].AbValue[0]+" "+AbBase[i].AbValue[1]+" "+AbBase[i].AbValue[2]+" "+AbBase[i].AbClass+" "+AbBase[i].Affinity);
            
            // System.out.println("Whole Affinity = "+Whole_Affinity(AbBase,Whole_Ag,AgScale));
             //System.out.println("Total number of iterations = "+iter_count);
     //}//end of runainet

             Antibody ab = new Antibody();
         for(int i=0;i<AgScale;i++)
        {
            equate(ab,AbBase[1]);
            for(int j=1;j<BaseScale;j++)
            {
                if(getAffinity(Whole_Ag[i],AbBase[j])>getAffinity(Whole_Ag[i],ab))
                {
                    Whole_Ag[i].AgClass=AbBase[j].AbClass;
                    equate(ab,AbBase[j]);
                }
            }
        }
         
    }

    protected  Image ainet(Image imageIn) throws Exception
{
    Dimension imageInDimension=getImageDimension(imageIn);
    int TRGB[][][] = pixelsArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
    Antigen [] Whole_Ag=new Antigen[(int)(imageInDimension.getHeight()*imageInDimension.getWidth())];
    ainet(Whole_Ag);
    int i=0;
        for(int row=0;row<imageInDimension.getHeight();row++)
        for (int column = 0; column < imageInDimension.getWidth(); column++)
        {
            if(Whole_Ag[i] != null)
            if(Whole_Ag[i].AgClass==1)
            {
                TRGB[1][column][row]=255;//55;
                TRGB[2][column][row]=222;//255;//128;
                TRGB[3][column][row]=10;//78;
            }
            else if(Whole_Ag[i].AgClass==2)
            {
                TRGB[1][column][row]=0;
                TRGB[2][column][row]=0;//64;//75;
                TRGB[3][column][row]=215;//78;
            }
            else if(Whole_Ag[i].AgClass==3)
            {
                TRGB[1][column][row]=0;
                TRGB[2][column][row]=215;
                TRGB[3][column][row]=0;
            }
            else if(Whole_Ag[i].AgClass==4)
            {
                TRGB[1][column][row]=215;
                TRGB[2][column][row]=0;
                TRGB[3][column][row]=0;
            }
            else if(Whole_Ag[i].AgClass==5)
            {
                TRGB[1][column][row]=0;
                TRGB[2][column][row]=0;
                TRGB[3][column][row]=0;
            }

            i++;

        }


    /*for(int row=0;row<imageInDimension.getHeight();row++)
        for (int column = 0; column < imageInDimension.getWidth(); column++)
        {
            System.out.println(TRGB[1][column][row]+"  "+TRGB[2][column][row]+"  "+TRGB[3][column][row]+"  3");

        }
    System.out.println(imageInDimension.getHeight()*imageInDimension.getWidth());*/
    return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);
}

  /*  protected Image mirror(Image imageIn){
        int[] pixel = imageToPixelsArray(imageIn);
        Dimension imageInDimension = getImageDimension(imageIn);
        for(int i=0; i<imageInDimension.getHeight(); i++)
            for(int j=0; j<imageInDimension.getWidth()/2; j++){
                int temp = pixel[i*(int)imageInDimension.getWidth()+j];
                pixel[i*(int)imageInDimension.getWidth()+j] = pixel[i*(int)imageInDimension.getWidth()+(int)imageInDimension.getWidth()-j-1];
                pixel[i*(int)imageInDimension.getWidth()+(int)imageInDimension.getWidth()-j-1] = temp;
        }
        return pixelsArrayToImage(pixel, imageInDimension);
    }*/

    protected Image rotate90(Image imageIn){
        int[] pixel = imageToPixelsArray(imageIn);
        int[] pixelsOut = new int[pixel.length];
        Dimension imageInDimension = getImageDimension(imageIn);
        Dimension imageOutDimension = new Dimension((int)imageInDimension.getHeight(), (int)imageInDimension.getWidth());
        for(int i=0; i<imageInDimension.getWidth(); i++)
            for(int j=0; j<imageInDimension.getHeight(); j++){
                pixelsOut[i*(int)imageInDimension.getHeight()+j] = pixel[pixel.length-((j+1)*(int)imageInDimension.getWidth())+i];
        }
        return pixelsArrayToImage(pixelsOut, imageOutDimension);
    }

    protected Image rotate180(Image imageIn){
        int[] pixel = imageToPixelsArray(imageIn);
        Dimension imageInDimension = getImageDimension(imageIn);
        for(int i=0; i<imageInDimension.getHeight()/2; i++)
            for(int j=0; j<imageInDimension.getWidth(); j++){
                int temp = pixel[i*(int)imageInDimension.getWidth()+j];
                pixel[i*(int)imageInDimension.getWidth()+j] = pixel[pixel.length-((i+1)*(int)imageInDimension.getWidth())+j];
                pixel[pixel.length-((i+1)*(int)imageInDimension.getWidth())+j] = temp;
        }
        return pixelsArrayToImage(pixel, imageInDimension);
    }

    protected Image rotate270(Image imageIn){
        int[] pixel = imageToPixelsArray(imageIn);
        int[] pixelsOut = new int[pixel.length];
        Dimension imageInDimension = getImageDimension(imageIn);
        Dimension imageOutDimension = new Dimension((int)imageInDimension.getHeight(), (int)imageInDimension.getWidth());
        for(int i=0; i<imageInDimension.getWidth(); i++)
            for(int j=0; j<imageInDimension.getHeight(); j++){
                pixelsOut[i*(int)imageInDimension.getHeight()+j] = pixel[(int)imageInDimension.getWidth()*(j+1)-(i+1)];
        }
        return pixelsArrayToImage(pixelsOut, imageOutDimension);
    }

    protected BufferedImage sharpen(Image imageIn){
        Dimension dimension = getImageDimension(imageIn);
        BufferedImage bufferedImage = new BufferedImage((int)dimension.getWidth(), (int)dimension.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(imageIn, null, null);
        float[] k = {0f, -1f, 0f, -1f, 5f, -1f, 0f, -1f, 0f};
        Kernel kernel = new Kernel(3,3, k);
        ConvolveOp cOp = new ConvolveOp(kernel);
        BufferedImage destination = cOp.createCompatibleDestImage(bufferedImage, bufferedImage.getColorModel());
        bufferedImage = cOp.filter(bufferedImage, destination);
        return destination;
    }

    protected Image derivativeFilter(Image imageIn){
        Dimension imageInDimension = getImageDimension(imageIn);
        int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int altTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        for (int colorComponent = 1; colorComponent < 4; colorComponent++)
            for (int x = 0; x < imageInDimension.getWidth(); x++)
                for (int y = 0; y < imageInDimension.getHeight(); y++){
                    int fx = 0;
                    int fy = 0;
                    if((x-1>0)&&(x+1<imageInDimension.getWidth()))
                        fx=TRGB[colorComponent][x-1][y]-TRGB[colorComponent][x+1][y];
                    if((y-1>0)&&(y+1<imageInDimension.getHeight()))
                        fy=TRGB[colorComponent][x][y-1]-TRGB[colorComponent][x][y+1];
                    altTRGB[colorComponent][x][y]=(int)Math.sqrt(Math.pow((fy),2)+Math.pow((fx),2));
                }
            return pixelsArrayToImage(TRGBArrayToPixelsArray(altTRGB, imageInDimension), imageInDimension);
	}

    protected Image robertsOperator(Image imageIn){
        Dimension imageInDimension = getImageDimension(imageIn);
        int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int altTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        for (int colorComponent = 1; colorComponent < 4; colorComponent++)
            for (int x = 0; x < imageInDimension.getWidth(); x++)
                for (int y = 0; y < imageInDimension.getHeight(); y++){
                    if((x+1<imageInDimension.getWidth()) && (y+1<imageInDimension.getHeight()))
                        altTRGB[colorComponent][x][y] = Math.abs(TRGB[colorComponent][x][y]-TRGB[colorComponent][x+1][y+1])+
                                Math.abs(TRGB[colorComponent][x][y+1]-TRGB[colorComponent][x+1][y]);
                }
            return pixelsArrayToImage(TRGBArrayToPixelsArray(altTRGB, imageInDimension), imageInDimension);
	}

    protected Image houghTransform(Image imageIn){
        Dimension dimension = getImageDimension(imageIn);
        int w = (int)dimension.getWidth(), h = (int)dimension.getHeight();
        BufferedImage bufferedImage = new BufferedImage((int)dimension.getWidth(), (int)dimension.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(imageIn, null, null);
        float[] k = { 0f, 0f, -1f, 0f, 0f, 0f, -1f, -2f, -1f, 0f, -1f, -2f, 16f, -2f, -1f, 0f, -1f, -2f, -1f, 0f, 0f, 0f, -1f, 0f, 0f};
        Kernel kernel = new Kernel(5,5, k);
        ConvolveOp cOp = new ConvolveOp(kernel);
        BufferedImage destination = cOp.createCompatibleDestImage(bufferedImage, bufferedImage.getColorModel());
        bufferedImage = cOp.filter(bufferedImage, destination);
        int[] pixel = imageToPixelsArray(destination);
        int[][] binaryImage = new int[w][h];
        int threshold = 100;
        for(int i = 0; i<pixel.length; i++){
            if(getRedComponent(pixel[i])>threshold || getGreenComponent(pixel[i])>threshold || getBlueComponent(pixel[i])> threshold)
                binaryImage[i%w][i/w] = 1;
        }

        class HoughHolder{
           int strength;
           int sumX, sumY;
           double meanX, meanY;
           double sumDiffMeanX, sumDiffMeanY;
           double stdevX, stdevY;

           ArrayList<Point> pointArray = new ArrayList<Point>();

            HoughHolder(int x, int y){
                strength = 1;
                pointArray.add(new Point(x, y));
            /*    sumX = x;
                sumY = y;
                meanX = x;
                meanY = y;*/
            }

            void insertPoint(int x, int y){
               // if(pointArray.size() < 50){
                    strength++;
                    pointArray.add(new Point(x, y));
           /*         sumX += x;
                    sumY += y;
                    meanX = sumX/pointArray.size();
                    meanY = sumY/pointArray.size();
                    sumDiffMeanX += (x-meanX)*(x-meanX);
                    sumDiffMeanY += (y-meanX)*(y-meanY);
                }
                else{
                    stdevX = Math.sqrt(sumDiffMeanX);
                    stdevY = Math.sqrt(sumDiffMeanY);
                    if(x-meanX < 2*stdevX && y-meanY < 2*stdevY){
                        strength++;
                        pointArray.add(new Point(x, y));
                        sumX += x;
                        sumY += y;
                        meanX = sumX/pointArray.size();
                        meanY = sumY/pointArray.size();
                        sumDiffMeanX += (x-meanX)*(x-meanX);
                        sumDiffMeanY += (y-meanX)*(y-meanY);
                    }
                }*/
            }

            int getStrength(){
                return strength;
            }

            int[] getLine(){
                double error = 2;
                double sumX =0, sumY=0, sumXX=0, sumXY=0;
                double n = (double)pointArray.size();
                double maxX = Double.MIN_VALUE, minX = Double.MAX_VALUE;
                double maxY = 0, minY = 0;

                for(int i=0; i<n; i++){
                    double x = pointArray.get(i).getX();
                    double y = pointArray.get(i).getY();
                    sumX += x;
                    sumY += y;
                    sumXX += x*x;
                    sumXY += x*y;
                }

                double slope = (n*sumXY-sumX*sumY)/(n*sumXX-sumX*sumX);
                double intercept = ((1/n)*sumY)-(slope*(1/n)*sumX);

                for(int i=0; i<n; i++){
                    double x = pointArray.get(i).getX();
                    double y = pointArray.get(i).getY();
                    if(x > maxX && ((y-slope*x) > (intercept - error)) && ((y-slope*x) < (intercept + error))){
                        maxX = x;
                        maxY = y;
                    }
                    else if(x < minX && ((y-slope*x) > (intercept - error)) && ((y-slope*x) < (intercept + error))){
                        minX = x;
                        minY = y;
                    }
                }

                int[] result = {(int)maxX, (int)maxY, (int)minX, (int)minY};
                return result;
            }
        }

        int xCenter = w/2, yCenter = h/2;
        int numStepsAng = 300, numStepsRad = 300;
        double angleIncr = Math.PI/numStepsAng;
        int centerRad = numStepsRad/2;
        double maxRad = Math.sqrt(xCenter*xCenter + yCenter*yCenter);
        double radIncr = (2.0*maxRad) / numStepsRad;
        HoughHolder[][] houghArray = new HoughHolder[numStepsAng][numStepsRad];

        for(int v=0; v<h; v++){
            for(int u=0; u<w; u++){
                if(binaryImage[u][v] > 0){
                    int x = u-xCenter, y = v-yCenter;
                    for(int i=0; i<numStepsAng; i++){
                        double theta = angleIncr * i;
                        int r = centerRad + (int)Math.rint((x*Math.cos(theta) + y*Math.sin(theta)) / radIncr);
                        if(r >= 0 && r < numStepsRad){
                            if(houghArray[i][r] == null)
                                houghArray[i][r] = new HoughHolder(u,v);
                            else
                                houghArray[i][r].insertPoint(u, v);
                        }
                    }
                }
            }
        }

        for(int i=0; i<houghArray.length; i++){
            for(int j=0; j<houghArray[0].length; j++){
                if((houghArray[i][j] != null) && (i+1 < houghArray.length) && (j+1 < houghArray[0].length) && (i-1 >= 0) && (j-1 >= 0)){
                    if( ((houghArray[i+1][j] != null) && (houghArray[i][j].getStrength()<houghArray[i+1][j].getStrength()))
                            || ((houghArray[i-1][j] != null) && (houghArray[i][j].getStrength()<houghArray[i-1][j].getStrength()))
                            || ((houghArray[i][j+1] != null) && (houghArray[i][j].getStrength()<houghArray[i][j+1].getStrength()))
                            || ((houghArray[i][j-1] != null) && (houghArray[i][j].getStrength()<houghArray[i][j-1].getStrength())) ){

                        houghArray[i][j] = null;
                    }


                }
                else{
                    houghArray[i][j] = null;
                }
            }
        }

        Graphics2D g2 = destination.createGraphics();
        g2.drawImage(destination, null, null);
        g2.setColor(Color.yellow);
        for(int i=0; i<houghArray.length; i++){
            for(int j=0; j<houghArray[0].length; j++){
                if(houghArray[i][j] != null && houghArray[i][j].getStrength() > 100){
                    int[] l = houghArray[i][j].getLine();
                    g2.drawLine(l[0], l[1], l[2], l[3]);
                }
            }
        }

        return destination;
    }


    protected Image smooth(Image imageIn){
        Dimension imageInDimension = getImageDimension(imageIn);
        int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int altTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        for (int colorComponent = 1; colorComponent < 4; colorComponent++)
            for (int x = 0; x < imageInDimension.getHeight(); x++)
                for (int y = 0; y < imageInDimension.getWidth(); y++){
                    if((x-1>0)&&(x+1<imageInDimension.getHeight())&&(y-1>0)&&(y+1<imageInDimension.getWidth()))
                        altTRGB[colorComponent][y][x]=Math.round(((float)TRGB[colorComponent][y][x-1]+
                                (float)TRGB[colorComponent][y][x]+(float)TRGB[colorComponent][y][x+1]+
                                (float)TRGB[colorComponent][y+1][x]+(float)TRGB[colorComponent][y-1][x])/5f);
        }
        return pixelsArrayToImage(TRGBArrayToPixelsArray(altTRGB, imageInDimension), imageInDimension);
    }

    protected Image histogram(Image imageIn){
        Dimension imageInDimension = getImageDimension(imageIn);
        int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int altTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int[] histR = new int[256], histG = new int[256], histB = new int[256];

        for (int x = 0; x < imageInDimension.getWidth(); x++)
                for (int y = 0; y < imageInDimension.getHeight(); y++){
                    histR[TRGB[1][x][y]]++;
                    histG[TRGB[2][x][y]]++;
                    histB[TRGB[3][x][y]]++;
        }

        int Rmin = histR[0], Gmin = histG[0], Bmin = histB[0];
        for(int i=1; i<256; i++){
            if(histR[i]<Rmin)
                Rmin = histR[i];
            if(histG[i]<Gmin)
                Gmin = histG[i];
            if(histB[i]<Bmin)
                Bmin = histB[i];

            histR[i] = histR[i-1] + histR[i];
            histG[i] = histG[i-1] + histG[i];
            histB[i] = histB[i-1] + histB[i];
        }

        for (int x = 0; x < imageInDimension.getWidth(); x++)
            for (int y = 0; y < imageInDimension.getHeight(); y++){
                altTRGB[1][x][y]=Math.round((((float)histR[TRGB[1][x][y]]-Rmin)/(float)(imageInDimension.getHeight() * imageInDimension.getWidth()-Rmin))*255);
                altTRGB[2][x][y]=Math.round((((float)histG[TRGB[2][x][y]]-Gmin)/(float)(imageInDimension.getHeight() * imageInDimension.getWidth()-Gmin))*255);
                altTRGB[3][x][y]=Math.round((((float)histB[TRGB[3][x][y]]-Bmin)/(float)(imageInDimension.getHeight() * imageInDimension.getWidth()-Bmin))*255);
            }
        return pixelsArrayToImage(TRGBArrayToPixelsArray(altTRGB, imageInDimension), imageInDimension);
    }

    protected Image colorToGrayscale(Image imageIn){
	Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        for (int row = 0; row < imageInDimension.getHeight(); row++)
            for (int column = 0; column < imageInDimension.getWidth(); column++){
		int average = (TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row]) / 3;
                TRGB[1][column][row] = average;
		TRGB[2][column][row] = average;
		TRGB[3][column][row] = average;
	}
	return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);
    }

    protected Image contrastStretching(Image imageIn, double low, double high){
    	Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int[] histogram = new int[256];
        int fromBottom = Math.round((float)(low * 0.01*imageInDimension.getHeight()*imageInDimension.getWidth()));
        int fromTop = Math.round((float)(high * 0.01*imageInDimension.getHeight()*imageInDimension.getWidth()));
        for (int row = 0; row < imageInDimension.getHeight(); row++)
            for (int column = 0; column < imageInDimension.getWidth(); column++){
		int average = (TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row]) / 3;
		TRGB[1][column][row] = average;
		TRGB[2][column][row] = average;
        	TRGB[3][column][row] = average;
                histogram[average]++;
        }
        boolean notFinished = true, lowFound = false, highFound = false;
        int aLow = -1, aHigh = -1, i = 0, lowerSum = 0, upperSum =0;
        while(notFinished){
            lowerSum += histogram[i];
            upperSum += histogram[255-i];
            if(lowerSum >= fromBottom && !lowFound){
                aLow = i;
                lowFound = true;
            }
            if(upperSum >= fromTop && !highFound){
                aHigh = 255-i;
                highFound = true;
            }
            if((aLow != -1) && (aHigh != -1))
                notFinished = false;
            i++;
        }
        for (int row = 0; row < imageInDimension.getHeight(); row++)
            for (int column = 0; column < imageInDimension.getWidth(); column++){
		int returnValue, aIn = TRGB[1][column][row];
		if(aIn <= aLow)
                    returnValue = 0;
                else if(aIn >= (aHigh))
                    returnValue = 255;
                else
                    returnValue = Math.round((255*(aIn - aLow))/(aHigh-aLow));
                TRGB[1][column][row] = returnValue;
		TRGB[2][column][row] = returnValue;
		TRGB[3][column][row] = returnValue;
        }
	return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);
    }

    protected Image kMeansSegmentation(Image imageIn, int numSegments, int colors, boolean colorSource){
        class ColorsClusterIndex{
            private int color1, color2, color3, cluster, index;
            public ColorsClusterIndex(int one, int two, int three, int c, int i){
                color1 = one;
                color2 = two;
                color3 = three;
                cluster = c;
                index = i;
            }
            public int getC1(){ return color1; }
            public int getC2(){ return color2; }
            public int getC3(){ return color3; }
            public int getCluster(){ return cluster; }
            public ColorsClusterIndex setCluster(int in){
                cluster = in;
                return this;
            }
            public int getIndex(){
                return index;
            }
        }

        Dimension imageInDimension = getImageDimension(imageIn);
        Random r = new Random();
        int pixel[] = imageToPixelsArray(imageIn);
        ArrayList<ColorsClusterIndex>[] kArray = new ArrayList[numSegments];

        for(int i=0; i<pixel.length; i++)
        {
            int c1, c2, c3;

            if(colors == 0)
            {
                c1 = getRedComponent(pixel[i]);
                c2 = getGreenComponent(pixel[i]);
                c3 = getBlueComponent(pixel[i]);
            }

            else if (colors == 1)
            {
                c1 = getLumaComponent(pixel[i]);
                c2 = getBlueDifference(pixel[i]);
                c3 = getRedDifference(pixel[i]);
            }

            else if (colors == 2)
            {
                c1 = getYComponent(pixel[i]);
                c2 = getUComponent(pixel[i]);
                c3 = getVComponent(pixel[i]);
            }

            else if (colors == 3)
            {
                float[] hsb = getHSBComponents(pixel[i]);
                c1 = (int)(hsb[0]*1000);
                c2 = (int)(hsb[1]*1000);
                c3 = (int)(hsb[2]*1000);
            }

            else if (colors == 4)
            {
                float[] xyz = getXYZComponents(pixel[i]);
                c1 = (int)(xyz[0]*1000);
                c2 = (int)(xyz[1]*1000);
                c3 = (int)(xyz[2]*1000);
            }

            else
            {
                float[] luv = getLUVComponents(pixel[i]);
                c1 = (int)(luv[0]*1000);
                c2 = (int)(luv[1]*1000);
                c3 = (int)(luv[2]*1000);
            }

            int rand = r.nextInt(numSegments);
            if(kArray[rand] == null)
                kArray[rand] = new ArrayList<ColorsClusterIndex>();
            ColorsClusterIndex temp = new ColorsClusterIndex(c1, c2, c3, rand, i);
            kArray[rand].add(temp);
        }

        boolean changes = true;
        double[][] means = new double[numSegments][3];

        while (changes)
        {
            changes = false;
            int sumC1, sumC2, sumC3;

            for(int i = 0; i<numSegments; i++){
                sumC1 = 0; sumC2 =0; sumC3 = 0;
                if(kArray[i] != null){
                    for(int j = 0; j < kArray[i].size(); j++){
                        sumC1 += kArray[i].get(j).getC1();
                        sumC2 += kArray[i].get(j).getC2();
                        sumC3 += kArray[i].get(j).getC3();
                    }
                means[i][0] = (double)sumC1/(double)kArray[i].size();
                means[i][1] = (double)sumC2/(double)kArray[i].size();
                means[i][2] = (double)sumC3/(double)kArray[i].size();
                }
            }

            ArrayList<ColorsClusterIndex>[] tempArray = new ArrayList[numSegments];
            for(int i = 0; i<numSegments; i++){
                if(kArray[i] != null){
                    int size = kArray[i].size()-1;
                    for(int j = size; j >= 0; j--){

                        double minAveDiff = Float.MAX_VALUE;
                        int index = -1;
                        for(int k = 0; k<numSegments; k++){
                            int sumDiff = 0;
                            sumDiff += Math.pow((kArray[i].get(j).getC1()-means[k][0]),2);
                            sumDiff += Math.pow((kArray[i].get(j).getC2()-means[k][1]),2);
                            sumDiff += Math.pow((kArray[i].get(j).getC3()-means[k][2]),2);
                            if(sumDiff< minAveDiff){
                                minAveDiff = sumDiff;
                                index = k;
                            }
                        }
                        if(index != kArray[i].get(j).getCluster())
                        changes = true;
                        if(tempArray[index] == null)
                            tempArray[index] = new ArrayList<ColorsClusterIndex>();
                        tempArray[index].add(kArray[i].get(j).setCluster(index));
                        kArray[i].remove(j);
                    }
                }
            }
            kArray = tempArray;
        }

        for(int i = 0; i<numSegments; i++)
        {
            int rd=0,gr=0,bl=0;
            if(!colorSource && colors == 0){
                rd = (int)means[i][0];
                gr = (int)means[i][1];
                bl = (int)means[i][2];
            }

            else
            {
                if(i==0){
                    rd = 255; gr = 0; bl = 0;}
                else if(i==1){
                    rd = 0; gr = 255; bl = 0;}
                else if(i==2){
                    rd = 0; gr = 0; bl = 255;}
                else if(i==3){
                    rd = 255; gr = 255; bl = 255;}
                else if(i==4){
                    rd = 0; gr = 0; bl = 0;}
                else if(i==5){
                    rd = 255; gr = 255; bl = 0;}
                else if(i==6){
                    rd = 0; gr = 255; bl = 255;}
                else if(i==7){
                    rd = 255; gr = 0; bl = 255;}
                else{
                    rd = r.nextInt(256);
                    gr = r.nextInt(256);
                    bl = r.nextInt(256);}
            }

            if(kArray[i] != null){
                for(int j = 0; j<kArray[i].size(); j++)
                {
                    pixel[kArray[i].get(j).getIndex()] = getTRGB(255,rd,gr,bl);
                }
            }
        }

        return pixelsArrayToImage(pixel, imageInDimension);
    }


    protected Image fuzzyCMeansSegmentation(Image imageIn, int numSegments, double fuzzyness, int color, boolean stopCondition, double stopValue)
    {
        Dimension imageInDimension = getImageDimension(imageIn);
        Random rand = new Random();
        int pixel[] = imageToPixelsArray(imageIn);
        int pixelComponents[][] = new int[pixel.length][3];
        double [][] u = new double[pixel.length][numSegments];
        double[] sum = new double[pixel.length];
        double[][] means = new double[numSegments][3];
        int numPasses;
        double epsilon;
        boolean epsilonStop = false;

        if(stopCondition){
            epsilon = stopValue;
            numPasses = -1;
            epsilonStop = true;
        }

        else{
            numPasses = (int) stopValue;
            epsilon = -1;
        }

        for(int i = 0; i<pixel.length; i++){
            if (color == 0){
                pixelComponents[i][0] = getRedComponent(pixel[i]);
                pixelComponents[i][1] = getGreenComponent(pixel[i]);
                pixelComponents[i][2] = getBlueComponent(pixel[i]);
            }

            else if (color == 1){
                pixelComponents[i][0] = getLumaComponent(pixel[i]);
                pixelComponents[i][1] = getBlueDifference(pixel[i]);
                pixelComponents[i][2] = getRedDifference(pixel[i]);
            }

            else if (color == 2){
                pixelComponents[i][0] = getYComponent(pixel[i]);
                pixelComponents[i][1] = getUComponent(pixel[i]);
                pixelComponents[i][2] = getVComponent(pixel[i]);
            }

            else if (color == 3){
                float[] hsb = getHSBComponents(pixel[i]);
                pixelComponents[i][0] = (int)(hsb[0]*1000);
                pixelComponents[i][1] = (int)(hsb[1]*1000);
                pixelComponents[i][2] = (int)(hsb[2]*1000);
            }

            else if (color == 4){
                float[] xyz = getXYZComponents(pixel[i]);
                pixelComponents[i][0] = (int)(xyz[0]*1000);
                pixelComponents[i][1] = (int)(xyz[1]*1000);
                pixelComponents[i][2] = (int)(xyz[2]*1000);
            }

            else if (color == 5){
                float[] luv = getLUVComponents(pixel[i]);
                pixelComponents[i][0] = (int)(luv[0]*1000);
                pixelComponents[i][1] = (int)(luv[1]*1000);
                pixelComponents[i][2] = (int)(luv[2]*1000);
            }
        }

        //generating random membership values
        for(int i = 0; i<numSegments; i++){
            for(int j = 0; j<pixel.length; j++){
                double r = rand.nextDouble();
                sum[j] += r;
                u[j][i] = r;
            }
        }

        //making it so the membership values for a pixel accoss each of the clusters sum to 1
        for(int i = 0; i<numSegments; i++){
            for(int j = 0; j<pixel.length; j++){
                u[j][i] = u[j][i]/sum[j];
            }
        }

        //loop until none of the cluster means change greater than epsilon between iterations
        while(epsilonStop || numPasses>0){
            //calculating each cluster's mean from the membership values.
            for(int i = 0; i< numSegments; i++){
                double sumWeight = 0, sumC1 =0, sumC2 = 0, sumC3 = 0;
                double weight;
                for(int j = 0; j<pixel.length; j++){
                    weight = Math.pow(u[j][i], fuzzyness);
                    sumWeight += weight;
                    sumC1 += weight*pixelComponents[j][0];
                    sumC2 += weight*pixelComponents[j][1];
                    sumC3 += weight*pixelComponents[j][2];

                }
                  System.out.println(sumWeight);
                double meanRRRR = sumC1/sumWeight;
                double meanG = sumC2/sumWeight;
                double meanB = sumC3/sumWeight;

                //deciding if the stop condition has been met
                double euclidDist = Math.sqrt(Math.pow(meanRRRR-means[i][0], 2)+Math.pow(meanG-means[i][1], 2)+Math.pow(meanB-means[i][2], 2));
                if(euclidDist<epsilon)
                    epsilonStop = false;

                means[i][0] = meanRRRR;
                means[i][1] = meanG;
                means[i][2] = meanB;
                numPasses--;
                //System.out.println("sumWeight = " + sumWeight);
                //System.out.println("red = " + means[i][0]);
            }

            //updating the membership values
            for(int i = 0; i<numSegments; i++){
                for(int j = 0; j<pixel.length; j++){
                    double distSqTop = Math.sqrt(Math.pow(pixelComponents[j][0]-means[i][0], 2) + Math.pow(pixelComponents[j][1]-means[i][1], 2) + Math.pow(pixelComponents[j][2]-means[i][2], 2));
                    double uInverse = 0;
                    for (int h = 0; h<numSegments; h++){
                        double distSqBottom = Math.sqrt(Math.pow((pixelComponents[j][0]-means[h][0]), 2) + Math.pow((pixelComponents[j][1]-means[h][1]), 2) + Math.pow((pixelComponents[j][2]-means[h][2]), 2));
                        uInverse += Math.pow((distSqTop/distSqBottom),(2/(fuzzyness-1)));
                    }

                    u[j][i] = Math.pow(uInverse, -1);

                }
            }
        }

        int[][] colorValues = getColorValues(numSegments);

        int[][] minmax = new int[numSegments][6];
        for(int b = 0; b<numSegments; b++){
            for(int f = 0; f<6; f++){
                if(f%2 == 0)
                    minmax[b][f] = 256;
                else
                    minmax[b][f] = -1;
            }
        }

        for(int i=0; i<pixel.length; i++){
            double max = -1;
            int segment = -1;
            for(int j=0; j<numSegments; j++){
                if(max < u[i][j]){
                    max = u[i][j];
                    segment = j;
                }
            }

            if(minmax[segment][0] > pixelComponents[i][0])
                minmax[segment][0] = pixelComponents[i][0];
            else if(minmax[segment][1] < pixelComponents[i][0])
                minmax[segment][1] = pixelComponents[i][0];
            else if(minmax[segment][2] > pixelComponents[i][1])
                minmax[segment][2] = pixelComponents[i][1];
            else if(minmax[segment][3] < pixelComponents[i][1])
                minmax[segment][3] = pixelComponents[i][1];
            else if(minmax[segment][4] > pixelComponents[i][2])
                minmax[segment][4] = pixelComponents[i][2];
            else if(minmax[segment][5] < pixelComponents[i][2])
                minmax[segment][5] = pixelComponents[i][2];

            pixel[i] = getTRGB(colorValues[segment][0], colorValues[segment][1], colorValues[segment][2], colorValues[segment][3]);
        }
        for(int b = 0; b<numSegments; b++){
            for(int f = 0; f<6; f++){
                System.out.print(minmax[b][f]+",");
            }
        }

        return pixelsArrayToImage(pixel, imageInDimension);
    }
   protected Image MySuperKmeans(Image imageIn, int k){
         class ClusterIndex{
            private int R, G, B, cluster, index_x,index_y;
            public ClusterIndex(int red, int Green, int Blue, int c, int x, int y){
                R = red;
                G = Green;
                B = Blue;
                cluster = c;
                index_x = x;
                index_y = y;
            }
            public int getR(){ return R; }
            public int getG(){ return G; }
            public int getB(){ return B; }
            public int getCluster(){ return cluster; }
            public ClusterIndex setCluster(int in){
                cluster = in;
                return this;
            }
            public int getIndex_x(){
                return index_x;
            }
            public int getIndex_y(){
                return index_y;
            }
            public int getSum(){
                return R + G + B;
            }
        }

        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        Random r = new Random();
        int My_row  =(int)imageInDimension.getHeight();   // row number
        int My_column =(int)imageInDimension.getWidth();  // column number
        ArrayList<ClusterIndex>[] kArray = new ArrayList[k];
        double[][] numberCluster = new double[k][3];
      int[] compare = new int[k];
       for(int row = 1; row <= (int)(My_row); row++){
           for(int colum =1; colum<= (int)(My_column); colum++){
                        for(int seg = 0; seg < k; seg++){
                       if(kArray[(colum*row)%k] == null)
                             kArray[(colum*row)%k] = new ArrayList<ClusterIndex>();
                             ClusterIndex temp = new ClusterIndex(TRGB[1][colum-1][row-1], TRGB[2][colum-1][row-1], TRGB[3][colum-1][row-1], seg, row-1,colum-1);
                       kArray[(colum*row)%k].add(temp);

           }
      }
    }
        boolean Flag = true;
        ArrayList<ClusterIndex>[] TArray = new ArrayList[k];
        int Max_Loop = 3;
        int M =0;
        int[] compare2 = new int[k];

       while(Flag)
        {
            M++;
            if(M ==Max_Loop){
                break;
            }
       // Flag = false  ;


        for(int i = 0; i< k; i++){

             int s1, s2, s3;
                if(kArray[i] != null){
                    s1 = 0; s2 =0; s3 = 0;
                    for(int j = 0; j < kArray[i].size(); j++){
                        s1 += kArray[i].get(j).getR();
                        s2 += kArray[i].get(j).getG();
                        s3 += kArray[i].get(j).getB();
                    }

                numberCluster[i][0] = (double)s1/(double)kArray[i].size();
                numberCluster[i][1] = (double)s2/(double)kArray[i].size();
                numberCluster[i][2] = (double)s3/(double)kArray[i].size();
                }
         }
          for(int i = 0; i< k; i++){
                if(kArray[i] != null){
                    int size = kArray[i].size()-1;
                    for(int j = size; j >= 0; j--){

                        double Min = Float.MAX_VALUE;
                        int index = -1;
                        for(int seg = 0; seg< k; seg++){
                        double s1 = 0, s2 = 0, s3 = 0, result = 0, downS1, downS2,downS3;
                        double p1 = (double)((kArray[i].get(j).getR()+1)/(double)(kArray[i].get(j).getSum()+1));
                        double q1 = (numberCluster[seg][0]+1)/(numberCluster[seg][0]+numberCluster[seg][1]+numberCluster[seg][2]+1);
                        s1 = p1*Math.log(p1/q1);
                        downS1 = q1*Math.log(q1/p1);
                        double p2 = (double)((kArray[i].get(j).getG()+1)/(double)(kArray[i].get(j).getSum()+1));
                        double q2 = (numberCluster[seg][1]+1)/(numberCluster[seg][0]+numberCluster[seg][1]+numberCluster[seg][2]+1);
                        s2 = p2*Math.log(p2/q2);
                        downS2 = q2*Math.log(q2/p2);
                        double p3 = (double)((kArray[i].get(j).getB()+1)/(double)(kArray[i].get(j).getSum()+1));
                        double q3 = (numberCluster[seg][2]+1)/(numberCluster[seg][0]+numberCluster[seg][1]+numberCluster[seg][2]+1);
                        s3 = p3*Math.log(p3/q3);
                        downS3 = q3*Math.log(q3/p3);
                        result = (s1 + s2 + s3 + downS1 +downS2 + downS3);
                            if(result <= Min){
                                Min = result;
                                index = seg;
                            }
               //         System.out.println(" R= " + kArray[i].get(j).getR() + "G = " + kArray[i].get(j).getG() + "B = "+kArray[i].get(j).getB());
               //         System.out.println(" downS1 = " + downS1 + " downS2 = " + downS2);
                        }
              //          System.out.println(index);
//                        if(index !=  kArray[i].get(i).getCluster()){
//                            Flag = true;
//                        }
                        if(TArray[index] == null)
                            TArray[index] = new ArrayList<ClusterIndex>();
                        TArray[index].add(kArray[i].get(j).setCluster(index));
                        kArray[i].remove(j);
                        compare2[index] ++;

                    }
                }
          }
            kArray = TArray;
            int x = 0;
            for(int j= 0; j <k; j++){

                if (compare2[j] == compare[j]){
                    x++ ;
                }

            if (x == k){
                Flag = false ;
            }
            else
                compare = compare2;
            }
        }
       int rd, gr,bl;
        for(int i = 0; i< k; i++)
        {
                if(i==0){
                    rd = 255; gr = 0; bl = 0;}
                else if(i==1){
                    rd = 0; gr = 255; bl = 0;}
                else if(i==2){
                    rd = 0; gr = 0; bl = 255;}
                else if(i==3){
                    rd = 255; gr = 255; bl = 255;}
                else if(i==4){
                    rd = 0; gr = 0; bl = 0;}
                else if(i==5){
                    rd = 255; gr = 255; bl = 0;}
                else if(i==6){
                    rd = 0; gr = 255; bl = 255;}
                else if(i==7){
                    rd = 255; gr = 0; bl = 255;}
                else{
                    rd = r.nextInt(256);
                    gr = r.nextInt(256);
                    bl = r.nextInt(256);}
            if(kArray[i] != null){
                for(int j = 0; j< kArray[i].size(); j++)
                {
                    TRGB[1][kArray[i].get(j).getIndex_y()][kArray[i].get(j).getIndex_x()] = rd;
                    TRGB[2][kArray[i].get(j).getIndex_y()][kArray[i].get(j).getIndex_x()] = gr;
                    TRGB[3][kArray[i].get(j).getIndex_y()][kArray[i].get(j).getIndex_x()] = bl;
                }
            }
        }

      for(int i = 0 ; i< k ; i ++){
          System.out.println(i +"num. R = " + numberCluster[i][0] + "G = "+ numberCluster[i][1] + "B = " + numberCluster[i][2]);
          double sum =  numberCluster[i][0]+numberCluster[i][1]+numberCluster[i][2];
          double p1 = (numberCluster[i][0]/sum)*Math.log(numberCluster[i][0]/sum);
          double p2 = (numberCluster[i][1]/sum)*Math.log(numberCluster[i][1]/sum);
          double p3 = (numberCluster[i][2]/sum)*Math.log(numberCluster[i][2]/sum);
          double result =  -1*(p1+p2+p3);
          System.out.println(" Entropy = " + result);
      }



        return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);
    }
        protected Image MySuperkMeansSIMSegmetation(Image imageIn, int numSegments){
        class ColorsClusterIndex{
            private int color1, color2, color3, cluster, index;
            public ColorsClusterIndex(int one, int two, int three, int c, int i){
                color1 = one;
                color2 = two;
                color3 = three;
                cluster = c;
                index = i;
            }
            public int getC1(){ return color1; }
            public int getC2(){ return color2; }
            public int getC3(){ return color3; }
            public int getCluster(){ return cluster; }
            public ColorsClusterIndex setCluster(int in){
                cluster = in;
                return this;
            }
            public int getIndex(){
                return index;
            }
            public int getSum(){
                return color1 + color2 + color3;
            }
        }

        Dimension imageInDimension = getImageDimension(imageIn);
        Random r = new Random();
        int pixel[] = imageToPixelsArray(imageIn);
        ArrayList<ColorsClusterIndex>[] kArray = new ArrayList[numSegments];

        for(int i=0; i<pixel.length; i++)
        {
            int c1, c2, c3;
                c1 = getRedComponent(pixel[i]);
                c2 = getGreenComponent(pixel[i]);
                c3 = getBlueComponent(pixel[i]);
            int rand = r.nextInt(numSegments);
            if(kArray[rand] == null)
                kArray[rand] = new ArrayList<ColorsClusterIndex>();
            ColorsClusterIndex temp = new ColorsClusterIndex(c1, c2, c3, rand, i);
            kArray[rand].add(temp);
        }

        boolean changes = true;
        double[][] means = new double[numSegments][3];
        int flag = 0;
        int counter = 100000;
        while (changes)
        {
            changes = false;

            int sumC1, sumC2, sumC3;

            for(int i = 0; i<numSegments; i++){
                sumC1 = 0; sumC2 =0; sumC3 = 0;
                if(kArray[i] != null){
                    for(int j = 0; j < kArray[i].size(); j++){
                        sumC1 += kArray[i].get(j).getC1();
                        sumC2 += kArray[i].get(j).getC2();
                        sumC3 += kArray[i].get(j).getC3();
                    }
                means[i][0] = (double)sumC1/(double)kArray[i].size();
                means[i][1] = (double)sumC2/(double)kArray[i].size();
                means[i][2] = (double)sumC3/(double)kArray[i].size();
                }
            }

            ArrayList<ColorsClusterIndex>[] tempArray = new ArrayList[numSegments];
            for(int i = 0; i<numSegments; i++){
                if(kArray[i] != null){
                    int size = kArray[i].size()-1;
                    for(int j = size; j >= 0; j--){

                        double minAveDiff = Float.MAX_VALUE;
                        int index = -1;
                        for(int k = 0; k<numSegments; k++){
                        double s1 = 0, s2 = 0, s3 = 0, result = 0, downS1, downS2,downS3;
                        double p1;
                       if(kArray[i].get(j).getC1() == 0 ){
                           p1 = 1.0/(255+255+255);
                       }else{
                           p1 = (double)(kArray[i].get(j).getC1())/(kArray[i].get(j).getSum());
                       }

                       double q1;
                       if(means[k][0] == 0){
                          q1 = 1.0/(255+255+255);
                       }else{
                           q1 = (means[k][0])/(means[k][0]+means[k][1]+means[k][2]);
                       }
                        s1 = Math.abs(p1*(Math.log(p1/q1)/Math.log(2.0)));
                        downS1 = Math.abs(q1*(Math.log(q1/p1)/Math.log(2.0)));

                        double p2;
                       if(kArray[i].get(j).getC2()==0){
                           p2 = 1.0/(255+255+255);
                       }else{
                           p2 = (double)(kArray[i].get(j).getC2())/(kArray[i].get(j).getSum());
                       }
                        double q2;
                        if(means[k][1]==0){
                           q2 = 1.0/(255+255+255);
                        }else{
                           q2 = (means[k][1])/(means[k][0]+means[k][1]+means[k][2]);
                        }
                        s2 = Math.abs(p2*(Math.log(p2/q2)/Math.log(2.0)));
                        downS2 = Math.abs(q2*(Math.log(q2/p2)/Math.log(2.0)));

                        double p3

                                ;
                        if(kArray[i].get(j).getC3()==0){
                            p3 = 1.0/(255+255+255);
                        }else{
                            p3 = (double)(kArray[i].get(j).getC3())/(kArray[i].get(j).getSum());
                       }
                        double q3;
                        if(means[k][2]==0){
                            q3 = 1.0/(255+255+255);
                        }else{
                           q3 = (double)(means[k][2])/(means[k][0]+means[k][1]+means[k][2]);
                        }
                        s3 = Math.abs(p3*(Math.log(p3/q3)/Math.log(2.0)));
                       downS3 = Math.abs(q3*(Math.log(q3/p3)/Math.log(2.0)));
                       double sum = (kArray[i].get(j).getC3() + kArray[i].get(j).getC2()+kArray[i].get(j).getC1());
                       double sum2 = (means[k][0]+means[k][1]+means[k][2]);
                      double con = Math.abs((sum) - (sum2));
                      con = Math.pow(con, 2);
                      if(con == 0){
                           con = 1;
                       }
                       result =  Math.pow(s1 + s2 + s3 + downS1 +downS2 + downS3, 0.5);
                       result = con*(result)*10000;
                       if(result< minAveDiff){
                                minAveDiff = result;
                                index = k;
                          //      System.out.println("result = " + result);
                            }
                        }
                        //System.out.println("index = " + index);
                        if(index != kArray[i].get(j).getCluster()){
                             changes = true;

                        }
                       if(counter == 100050){
                              changes = false;
                        }
                        if(tempArray[index] == null)
                            tempArray[index] = new ArrayList<ColorsClusterIndex>();
                        tempArray[index].add(kArray[i].get(j).setCluster(index));
                        kArray[i].remove(j);
                    }
                }
            }
           // for()
            kArray = tempArray;
        counter++;

        }
       System.out.println(" counter = " + counter);
        for(int i = 0; i<numSegments; i++)
        {
          int rd,gr,bl;
                if(i==0){
                    rd = 255; gr = 0; bl = 0;}
                else if(i==1){
                    rd = 0; gr = 255; bl = 0;}
                else if(i==2){
                    rd = 0; gr = 0; bl = 255;}
                else if(i==3){
                    rd = 255; gr = 255; bl = 255;}
                else if(i==4){
                    rd = 0; gr = 0; bl = 0;}
                else if(i==5){
                    rd = 255; gr = 255; bl = 0;}
                else if(i==6){
                    rd = 0; gr = 255; bl = 255;}
                else if(i==7){
                    rd = 255; gr = 0; bl = 255;}
                else{
                    rd = r.nextInt(256);
                    gr = r.nextInt(256);
                    bl = r.nextInt(256);}


            if(kArray[i] != null){
                for(int j = 0; j<kArray[i].size(); j++)
                {
                    pixel[kArray[i].get(j).getIndex()] = getTRGB(255,rd,gr,bl);
                }
            }
        }

        return pixelsArrayToImage(pixel, imageInDimension);
    }



     protected Image My_SAMSuperkMeansSegmetation(Image imageIn, int numSegments){
        class ColorsClusterIndex{
            private int color1, color2, color3, cluster, index;
            public ColorsClusterIndex(int one, int two, int three, int c, int i){
                color1 = one;
                color2 = two;
                color3 = three;
                cluster = c;
                index = i;
            }
            public int getC1(){ return color1; }
            public int getC2(){ return color2; }
            public int getC3(){ return color3; }
            public int getCluster(){ return cluster; }
            public ColorsClusterIndex setCluster(int in){
                cluster = in;
                return this;
            }
            public int getIndex(){
                return index;
            }
            public int getSum(){
                return color1 + color2 + color3;
            }
        }

        Dimension imageInDimension = getImageDimension(imageIn);
        Random r = new Random();
        int pixel[] = imageToPixelsArray(imageIn);
        ArrayList<ColorsClusterIndex>[] kArray = new ArrayList[numSegments];

        for(int i=0; i<pixel.length; i++)
        {
            int c1, c2, c3;
                c1 = getRedComponent(pixel[i]);
                c2 = getGreenComponent(pixel[i]);
                c3 = getBlueComponent(pixel[i]);
            int rand = r.nextInt(numSegments);
            if(kArray[rand] == null)
                kArray[rand] = new ArrayList<ColorsClusterIndex>();
            ColorsClusterIndex temp = new ColorsClusterIndex(c1, c2, c3, rand, i);
            kArray[rand].add(temp);
        }

        boolean changes = true;
        double[][] means = new double[numSegments][3];
        int flag = 0;
        while (changes)
        {
            changes = false;
            int sumC1, sumC2, sumC3;

            for(int i = 0; i<numSegments; i++){
                sumC1 = 0; sumC2 =0; sumC3 = 0;
                if(kArray[i] != null){
                    for(int j = 0; j < kArray[i].size(); j++){
                        sumC1 += kArray[i].get(j).getC1();
                        sumC2 += kArray[i].get(j).getC2();
                        sumC3 += kArray[i].get(j).getC3();
                    }
                means[i][0] = (double)sumC1/(double)kArray[i].size();
                means[i][1] = (double)sumC2/(double)kArray[i].size();
                means[i][2] = (double)sumC3/(double)kArray[i].size();
                }
            }

            ArrayList<ColorsClusterIndex>[] tempArray = new ArrayList[numSegments];
            for(int i = 0; i<numSegments; i++){
                if(kArray[i] != null){
                    int size = kArray[i].size()-1;
                    for(int j = size; j >= 0; j--){

                        double minAveDiff = Float.MAX_VALUE;
                        int index = -1;
                        for(int k = 0; k<numSegments; k++){
                        double s1 = 0, s2 = 0, s3 = 0, result, downS1, downS2;
                        s1 = kArray[i].get(j).getC1()*means[k][0];
                        s2 = kArray[i].get(j).getC2()*means[k][1];
                        s3 = kArray[i].get(j).getC3()*means[k][2];
                        downS1 =Math.pow((double)kArray[i].get(j).getC1(),2) +
                                Math.pow((double)kArray[i].get(j).getC2(),2) +
                                Math.pow((double)kArray[i].get(j).getC3(),2);
                        downS1 = Math.sqrt(downS1);
                        downS2 =Math.pow((double)means[k][0],2) +
                                Math.pow((double)means[k][1],2) +
                                Math.pow((double)means[k][2],2);
                        downS2 = Math.sqrt(downS2);

                        result = (int)(Math.acos((s1 + s2 + s3)/(downS1*downS2))*1000000);
                      //  System.out.println(" s1 = " + s1 + " s2 = " + s2 + "s3 = " + s3);
                     //   System.out.println("downs1 = " + downS1 + " downS2 = " + downS2);
                            if(result< minAveDiff){
                                minAveDiff = result;
                                index = k;
                     //           System.out.println("result = " + result);
                            }
                        }
                   //     System.out.println("index = " + index);
                        if(index != kArray[i].get(j).getCluster()){
                             changes = true;

                        }
                        if(tempArray[index] == null)
                            tempArray[index] = new ArrayList<ColorsClusterIndex>();
                        tempArray[index].add(kArray[i].get(j).setCluster(index));
                        kArray[i].remove(j);
                    }
                }
            }
            kArray = tempArray;


        }

        for(int i = 0; i<numSegments; i++)
        {
          int rd,gr,bl;
                if(i==0){
                    rd = 255; gr = 0; bl = 0;}
                else if(i==1){
                    rd = 0; gr = 255; bl = 0;}
                else if(i==2){
                    rd = 0; gr = 0; bl = 255;}
                else if(i==3){
                    rd = 255; gr = 255; bl = 255;}
                else if(i==4){
                    rd = 0; gr = 0; bl = 0;}
                else if(i==5){
                    rd = 255; gr = 255; bl = 0;}
                else if(i==6){
                    rd = 0; gr = 255; bl = 255;}
                else if(i==7){
                    rd = 255; gr = 0; bl = 255;}
                else{
                    rd = r.nextInt(256);
                    gr = r.nextInt(256);
                    bl = r.nextInt(256);}


            if(kArray[i] != null){
                for(int j = 0; j<kArray[i].size(); j++)
                {
                    pixel[kArray[i].get(j).getIndex()] = getTRGB(255,rd,gr,bl);
                }
            }
        }

        return pixelsArrayToImage(pixel, imageInDimension);
    }
      protected Image My_SIMTANSuperkMeansSegmetation(Image imageIn, int numSegments){
        class ColorsClusterIndex{
            private int color1, color2, color3, cluster, index;
            public ColorsClusterIndex(int one, int two, int three, int c, int i){
                color1 = one;
                color2 = two;
                color3 = three;
                cluster = c;
                index = i;
            }
            public int getC1(){ return color1; }
            public int getC2(){ return color2; }
            public int getC3(){ return color3; }
            public int getCluster(){ return cluster; }
            public ColorsClusterIndex setCluster(int in){
                cluster = in;
                return this;
            }
            public int getIndex(){
                return index;
            }
            public int getSum(){
                return color1 + color2 + color3;
            }
        }

        Dimension imageInDimension = getImageDimension(imageIn);
        Random r = new Random();
        int pixel[] = imageToPixelsArray(imageIn);
        ArrayList<ColorsClusterIndex>[] kArray = new ArrayList[numSegments];

        for(int i=0; i<pixel.length; i++)
        {
            int c1, c2, c3;
                c1 = getRedComponent(pixel[i]);
                c2 = getGreenComponent(pixel[i]);
                c3 = getBlueComponent(pixel[i]);
            int rand = r.nextInt(numSegments);
            if(kArray[rand] == null)
                kArray[rand] = new ArrayList<ColorsClusterIndex>();
            ColorsClusterIndex temp = new ColorsClusterIndex(c1, c2, c3, rand, i);
            kArray[rand].add(temp);
        }

        boolean changes = true;
        double[][] means = new double[numSegments][3];
        int flag = 0;
        while (changes)
        {
            changes = false;
            int sumC1, sumC2, sumC3;

            for(int i = 0; i<numSegments; i++){
                sumC1 = 0; sumC2 =0; sumC3 = 0;
                if(kArray[i] != null){
                    for(int j = 0; j < kArray[i].size(); j++){
                        sumC1 += kArray[i].get(j).getC1();
                        sumC2 += kArray[i].get(j).getC2();
                        sumC3 += kArray[i].get(j).getC3();
                    }
                means[i][0] = (double)sumC1/(double)kArray[i].size();
                means[i][1] = (double)sumC2/(double)kArray[i].size();
                means[i][2] = (double)sumC3/(double)kArray[i].size();
                }
            }

            ArrayList<ColorsClusterIndex>[] tempArray = new ArrayList[numSegments];
            for(int i = 0; i<numSegments; i++){
                if(kArray[i] != null){
                    int size = kArray[i].size()-1;
                    for(int j = size; j >= 0; j--){

                        double minAveDiff = Float.MAX_VALUE;
                        int index = -1;
                        for(int k = 0; k<numSegments; k++){
                        double s1 = 0, s2 = 0, s3 = 0, result, downS1, downS2;
                        s1 = kArray[i].get(j).getC1()*means[k][0];
                        s2 = kArray[i].get(j).getC2()*means[k][1];
                        s3 = kArray[i].get(j).getC3()*means[k][2];
                        downS1 =Math.pow((double)kArray[i].get(j).getC1(),2) +
                                Math.pow((double)kArray[i].get(j).getC2(),2) +
                                Math.pow((double)kArray[i].get(j).getC3(),2);
                        downS1 = Math.sqrt(downS1);
                        downS2 =Math.pow((double)means[k][0],2) +
                                Math.pow((double)means[k][1],2) +
                                Math.pow((double)means[k][2],2);
                        downS2 = Math.sqrt(downS2);
                        result = (int)(Math.acos((s1 + s2 + s3)/(downS1*downS2))*1000);
                     //   System.out.println(" s1 = " + s1 + " s2 = " + s2 + "s3 = " + s3);
                     //   System.out.println("downs1 = " + downS1 + " downS2 = " + downS2);
                            if(result< minAveDiff){
                                minAveDiff = result;
                                index = k;
                   //             System.out.println("result = " + result);
                            }
                        }
                        System.out.println("index = " + index);
                        if(index != kArray[i].get(j).getCluster()){
                             changes = true;

                        }
                        if(tempArray[index] == null)
                            tempArray[index] = new ArrayList<ColorsClusterIndex>();
                        tempArray[index].add(kArray[i].get(j).setCluster(index));
                        kArray[i].remove(j);
                    }
                }
            }
            kArray = tempArray;


        }

        for(int i = 0; i<numSegments; i++)
        {
          int rd,gr,bl;
                if(i==0){
                    rd = 255; gr = 0; bl = 0;}
                else if(i==1){
                    rd = 0; gr = 255; bl = 0;}
                else if(i==2){
                    rd = 0; gr = 0; bl = 255;}
                else if(i==3){
                    rd = 255; gr = 255; bl = 255;}
                else if(i==4){
                    rd = 0; gr = 0; bl = 0;}
                else if(i==5){
                    rd = 255; gr = 255; bl = 0;}
                else if(i==6){
                    rd = 0; gr = 255; bl = 255;}
                else if(i==7){
                    rd = 255; gr = 0; bl = 255;}
                else{
                    rd = r.nextInt(256);
                    gr = r.nextInt(256);
                    bl = r.nextInt(256);}


            if(kArray[i] != null){
                for(int j = 0; j<kArray[i].size(); j++)
                {
                    pixel[kArray[i].get(j).getIndex()] = getTRGB(255,rd,gr,bl);
                }
            }
        }

        return pixelsArrayToImage(pixel, imageInDimension);
    }
  /*  protected Image My_EntropySuperkMeansSegmetation(Image imageIn, int numSegments){
        class ColorsClusterIndex{
            private int color1, color2, color3, cluster, index;
            public ColorsClusterIndex(int one, int two, int three, int c, int i){
                color1 = one;
                color2 = two;
                color3 = three;
                cluster = c;
                index = i;
            }
            public int getC1(){ return color1; }
            public int getC2(){ return color2; }
            public int getC3(){ return color3; }
            public int getCluster(){ return cluster; }
            public ColorsClusterIndex setCluster(int in){
                cluster = in;
                return this;
            }
            public int getIndex(){
                return index;
            }
            public int getSum(){
                return color1 + color2 + color3;
            }
        }

        Dimension imageInDimension = getImageDimension(imageIn);
        Random r = new Random();
        int pixel[] = imageToPixelsArray(imageIn);
        ArrayList<ColorsClusterIndex>[] kArray = new ArrayList[numSegments];

        for(int i=0; i<pixel.length; i++)
        {
            int c1, c2, c3;
                c1 = getRedComponent(pixel[i]);
                c2 = getGreenComponent(pixel[i]);
                c3 = getBlueComponent(pixel[i]);
            int rand = r.nextInt(numSegments);
            if(kArray[rand] == null)
                kArray[rand] = new ArrayList<ColorsClusterIndex>();
            ColorsClusterIndex temp = new ColorsClusterIndex(c1, c2, c3, rand, i);
            kArray[rand].add(temp);
        }

        boolean changes = true;
        double[][] means = new double[numSegments][3];
        int flag = 0;
        while (changes)
        {
            changes = false;
            int sumC1, sumC2, sumC3;

            for(int i = 0; i<numSegments; i++){
                sumC1 = 0; sumC2 =0; sumC3 = 0;
                if(kArray[i] != null){
                    for(int j = 0; j < kArray[i].size(); j++){
                        sumC1 += kArray[i].get(j).getC1();
                        sumC2 += kArray[i].get(j).getC2();
                        sumC3 += kArray[i].get(j).getC3();
                    }
                means[i][0] = (double)sumC1/(double)kArray[i].size();
                means[i][1] = (double)sumC2/(double)kArray[i].size();
                means[i][2] = (double)sumC3/(double)kArray[i].size();
                }
            }

            ArrayList<ColorsClusterIndex>[] tempArray = new ArrayList[numSegments];
            for(int i = 0; i<numSegments; i++){
                if(kArray[i] != null){
                    int size = kArray[i].size()-1;
                    for(int j = size; j >= 0; j--){

                        double minAveDiff = Float.MAX_VALUE,result = 0;
                        int index = -1;

                        double PR = (double)(kArray[i].get(j).getC1()+1)/(kArray[i].get(j).getSum()+1);
                        PR = PR* Math.log(PR);
                        double PG = (double)(kArray[i].get(j).getC2()+1)/(kArray[i].get(j).getSum()+1);
                        PG = PG* Math.log(PG);
                        double PB = (double)(kArray[i].get(j).getC3()+1)/(kArray[i].get(j).getSum()+1);
                        PB = PB* Math.log(PB);
                   //     System.out.println(" R = " + kArray[i].get(j).getC1() + " G = " + kArray[i].get(j).getC2() + " B = " + kArray[i].get(j).getC3() );
                        double ResultRGB = -1000000*(PR + PG + PB);
                   //     System.out.println(" resultRGB = " + ResultRGB);
                     for(int k = 0; k<numSegments; k++){
                        double s1 = 0, s2 = 0, s3 = 0, ResultK ;
                        double sum = 0;
                        sum = means[k][0]+means[k][1]+means[k][2]+1;

                        s1 = (means[k][0]+1)/sum;
                        s1 = s1*Math.log(s1);
                        s2 = (means[k][1]+1)/sum;
                        s2 = s2*Math.log(s2);
                        s3 = (means[k][2]+1)/sum;
                        s3 = s3*Math.log(s3);
                        ResultK = -1000000*(s1+s2+s3);
                   //     System.out.println(" resultk = "+ ResultK);
                        result = (int)(Math.abs((ResultK-ResultRGB)));
                   //     System.out.println(" result = " + result);
                     //   System.out.println("downs1 = " + downS1 + " downS2 = " + downS2);
                            if(result< minAveDiff){
                                minAveDiff = result;
                                index = k;
                   //             System.out.println("result = " + result);
                            }
                        }
                        System.out.println("index = " + index);
                        if(index != kArray[i].get(j).getCluster()){
                             changes = true;

                        }
                        if(tempArray[index] == null)
                            tempArray[index] = new ArrayList<ColorsClusterIndex>();
                        tempArray[index].add(kArray[i].get(j).setCluster(index));
                        kArray[i].remove(j);
                    }
                }
            }
            kArray = tempArray;


        }

        for(int i = 0; i<numSegments; i++)
        {
          int rd,gr,bl;
                if(i==0){
                    rd = 255; gr = 0; bl = 0;}
                else if(i==1){
                    rd = 0; gr = 255; bl = 0;}
                else if(i==2){
                    rd = 0; gr = 0; bl = 255;}
                else if(i==3){
                    rd = 255; gr = 255; bl = 255;}
                else if(i==4){
                    rd = 0; gr = 0; bl = 0;}
                else if(i==5){
                    rd = 255; gr = 255; bl = 0;}
                else if(i==6){
                    rd = 0; gr = 255; bl = 255;}
                else if(i==7){
                    rd = 255; gr = 0; bl = 255;}
                else{
                    rd = r.nextInt(256);
                    gr = r.nextInt(256);
                    bl = r.nextInt(256);}


            if(kArray[i] != null){
                for(int j = 0; j<kArray[i].size(); j++)
                {
                    pixel[kArray[i].get(j).getIndex()] = getTRGB(255,rd,gr,bl);
                }
            }
        }

        return pixelsArrayToImage(pixel, imageInDimension);
    }
*/
      /*
    protected Image My_SIMSuperkMeansSegmetation(Image imageIn, int numSegments){
        class ColorsClusterIndex{
            private int color1, color2, color3, cluster, index;
            public ColorsClusterIndex(int one, int two, int three, int c, int i){
                color1 = one;
                color2 = two;
                color3 = three;
                cluster = c;
                index = i;
            }
            public int getC1(){ return color1; }
            public int getC2(){ return color2; }
            public int getC3(){ return color3; }
            public int getCluster(){ return cluster; }
            public ColorsClusterIndex setCluster(int in){
                cluster = in;
                return this;
            }
            public int getIndex(){
                return index;
            }
            public int getSum(){
                return color1 + color2 + color3;
            }
        }

        Dimension imageInDimension = getImageDimension(imageIn);
        Random r = new Random();
        int pixel[] = imageToPixelsArray(imageIn);
        ArrayList<ColorsClusterIndex>[] kArray = new ArrayList[numSegments];

        for(int i=0; i<pixel.length; i++)
        {
            int c1, c2, c3;
                c1 = getRedComponent(pixel[i]);
                c2 = getGreenComponent(pixel[i]);
                c3 = getBlueComponent(pixel[i]);
            int rand = r.nextInt(numSegments);
            if(kArray[rand] == null)
                kArray[rand] = new ArrayList<ColorsClusterIndex>();
            ColorsClusterIndex temp = new ColorsClusterIndex(c1, c2, c3, rand, i);
            kArray[rand].add(temp);
        }

        boolean changes = true;
        double[][] means = new double[numSegments][3];
        int flag = 0;
        while (changes)
        {
            changes = false;
            int sumC1, sumC2, sumC3;

            for(int i = 0; i<numSegments; i++){
                sumC1 = 0; sumC2 =0; sumC3 = 0;
                if(kArray[i] != null){
                    for(int j = 0; j < kArray[i].size(); j++){
                        sumC1 += kArray[i].get(j).getC1();
                        sumC2 += kArray[i].get(j).getC2();
                        sumC3 += kArray[i].get(j).getC3();
                    }
                means[i][0] = (double)sumC1/(double)kArray[i].size();
                means[i][1] = (double)sumC2/(double)kArray[i].size();
                means[i][2] = (double)sumC3/(double)kArray[i].size();
                }
            }

            ArrayList<ColorsClusterIndex>[] tempArray = new ArrayList[numSegments];
            for(int i = 0; i<numSegments; i++){
                if(kArray[i] != null){
                    int size = kArray[i].size()-1;
                    for(int j = size; j >= 0; j--){

                        double minAveDiff = Float.MAX_VALUE;
                        int index = -1;
                        for(int k = 0; k<numSegments; k++){
                        double s1 = 0, s2 = 0, s3 = 0, result, downS1, downS2;
                        s1 = kArray[i].get(j).getC1()*means[k][0];
                        s2 = kArray[i].get(j).getC2()*means[k][1];
                        s3 = kArray[i].get(j).getC3()*means[k][2];
                        downS1 =Math.pow((double)kArray[i].get(j).getC1(),2) +
                                Math.pow((double)kArray[i].get(j).getC2(),2) +
                                Math.pow((double)kArray[i].get(j).getC3(),2);
                        downS1 = Math.sqrt(downS1);
                        downS2 =Math.pow((double)means[k][0],2) +
                                Math.pow((double)means[k][1],2) +
                                Math.pow((double)means[k][2],2);
                        downS2 = Math.sqrt(downS2);
                        result = (int)(Math.acos((s1 + s2 + s3)/(downS1*downS2))*1000);
                     //   System.out.println(" s1 = " + s1 + " s2 = " + s2 + "s3 = " + s3);
                     //   System.out.println("downs1 = " + downS1 + " downS2 = " + downS2);
                            if(result< minAveDiff){
                                minAveDiff = result;
                                index = k;
                   //             System.out.println("result = " + result);
                            }
                        }
                        System.out.println("index = " + index);
                        if(index != kArray[i].get(j).getCluster()){
                             changes = true;

                        }
                        if(tempArray[index] == null)
                            tempArray[index] = new ArrayList<ColorsClusterIndex>();
                        tempArray[index].add(kArray[i].get(j).setCluster(index));
                        kArray[i].remove(j);
                    }
                }
            }
            kArray = tempArray;


        }

        for(int i = 0; i<numSegments; i++)
        {
          int rd,gr,bl;
                if(i==0){
                    rd = 255; gr = 0; bl = 0;}
                else if(i==1){
                    rd = 0; gr = 255; bl = 0;}
                else if(i==2){
                    rd = 0; gr = 0; bl = 255;}
                else if(i==3){
                    rd = 255; gr = 255; bl = 255;}
                else if(i==4){
                    rd = 0; gr = 0; bl = 0;}
                else if(i==5){
                    rd = 255; gr = 255; bl = 0;}
                else if(i==6){
                    rd = 0; gr = 255; bl = 255;}
                else if(i==7){
                    rd = 255; gr = 0; bl = 255;}
                else{
                    rd = r.nextInt(256);
                    gr = r.nextInt(256);
                    bl = r.nextInt(256);}


            if(kArray[i] != null){
                for(int j = 0; j<kArray[i].size(); j++)
                {
                    pixel[kArray[i].get(j).getIndex()] = getTRGB(255,rd,gr,bl);
                }
            }
        }

        return pixelsArrayToImage(pixel, imageInDimension);
    }
*/
     protected Image MySpectralAngleMapper(Image imageIn, int numSegments){
        class ColorsClusterIndex{
            private int color1, color2, color3, cluster, index;
            public ColorsClusterIndex(int one, int two, int three, int c, int i){
                color1 = one;
                color2 = two;
                color3 = three;
                cluster = c;
                index = i;
            }
            public int getC1(){ return color1; }
            public int getC2(){ return color2; }
            public int getC3(){ return color3; }
            public int getCluster(){ return cluster; }
            public ColorsClusterIndex setCluster(int in){
                cluster = in;
                return this;
            }
            public int getIndex(){
                return index;
            }
            public int getSum(){
                return color1 + color2 + color3;
            }
        }

        Dimension imageInDimension = getImageDimension(imageIn);
        Random r = new Random();
        int pixel[] = imageToPixelsArray(imageIn);
        ArrayList<ColorsClusterIndex>[] kArray = new ArrayList[numSegments];

        for(int i=0; i<pixel.length; i++)
        {
            int c1, c2, c3;
                c1 = getRedComponent(pixel[i]);
                c2 = getGreenComponent(pixel[i]);
                c3 = getBlueComponent(pixel[i]);
            int rand = r.nextInt(numSegments);
            if(kArray[rand] == null)
                kArray[rand] = new ArrayList<ColorsClusterIndex>();
            ColorsClusterIndex temp = new ColorsClusterIndex(c1, c2, c3, rand, i);
            kArray[rand].add(temp);
        }

        boolean changes = true;
        double[][] means = new double[numSegments][3];
        int flag = 0;
        while (changes)
        {
            changes = false;
            int sumC1, sumC2, sumC3;

            for(int i = 0; i<numSegments; i++){
                sumC1 = 0; sumC2 =0; sumC3 = 0;
                if(kArray[i] != null){
                    for(int j = 0; j < kArray[i].size(); j++){
                        sumC1 += kArray[i].get(j).getC1();
                        sumC2 += kArray[i].get(j).getC2();
                        sumC3 += kArray[i].get(j).getC3();
                    }
                means[i][0] = (double)sumC1/(double)kArray[i].size();
                means[i][1] = (double)sumC2/(double)kArray[i].size();
                means[i][2] = (double)sumC3/(double)kArray[i].size();
                }
            }

            ArrayList<ColorsClusterIndex>[] tempArray = new ArrayList[numSegments];
            for(int i = 0; i<numSegments; i++){
                if(kArray[i] != null){
                    int size = kArray[i].size()-1;
                    for(int j = size; j >= 0; j--){

                        double minAveDiff = Float.MAX_VALUE;
                        int index = -1;
                        for(int k = 0; k<numSegments; k++){
                        double s1 = 0, s2 = 0, s3 = 0, result, downS1, downS2;
                        s1 = kArray[i].get(j).getC1()*means[k][0];
                        s2 = kArray[i].get(j).getC2()*means[k][1];
                        s3 = kArray[i].get(j).getC3()*means[k][2];
                        downS1 =Math.pow((double)kArray[i].get(j).getC1(),2) +
                                Math.pow((double)kArray[i].get(j).getC2(),2) +
                                Math.pow((double)kArray[i].get(j).getC3(),2);
                        downS1 = Math.sqrt(downS1);
                        downS2 =Math.pow((double)means[k][0],2) +
                                Math.pow((double)means[k][1],2) +
                                Math.pow((double)means[k][2],2);
                        downS2 = Math.sqrt(downS2);
                        result = (int)(Math.acos((s1 + s2 + s3)/(downS1*downS2))*1000);
                     //   System.out.println(" s1 = " + s1 + " s2 = " + s2 + "s3 = " + s3);
                     //   System.out.println("downs1 = " + downS1 + " downS2 = " + downS2);
                            if(result< minAveDiff){
                                minAveDiff = result;
                                index = k;
                   //             System.out.println("result = " + result);
                            }
                        }
                   //     System.out.println("index = " + index);
                        if(index != kArray[i].get(j).getCluster()){
                             changes = true;

                        }
                        if(tempArray[index] == null)
                            tempArray[index] = new ArrayList<ColorsClusterIndex>();
                        tempArray[index].add(kArray[i].get(j).setCluster(index));
                        kArray[i].remove(j);
                    }
                }
            }
            kArray = tempArray;


        }

        for(int i = 0; i<numSegments; i++)
        {
          int rd,gr,bl;
                if(i==0){
                    rd = 255; gr = 0; bl = 0;}
                else if(i==1){
                    rd = 0; gr = 255; bl = 0;}
                else if(i==2){
                    rd = 0; gr = 0; bl = 255;}
                else if(i==3){
                    rd = 255; gr = 255; bl = 255;}
                else if(i==4){
                    rd = 0; gr = 0; bl = 0;}
                else if(i==5){
                    rd = 255; gr = 255; bl = 0;}
                else if(i==6){
                    rd = 0; gr = 255; bl = 255;}
                else if(i==7){
                    rd = 255; gr = 0; bl = 255;}
                else{
                    rd = r.nextInt(256);
                    gr = r.nextInt(256);
                    bl = r.nextInt(256);}


            if(kArray[i] != null){
                for(int j = 0; j<kArray[i].size(); j++)
                {
                    pixel[kArray[i].get(j).getIndex()] = getTRGB(255,rd,gr,bl);
                }
            }
        }

        return pixelsArrayToImage(pixel, imageInDimension);
    }


    protected Image MySpectralInformationDivergence(Image imageIn, int numSegments){
        class ColorsClusterIndex{
            private int color1, color2, color3, cluster, index;
            public ColorsClusterIndex(int one, int two, int three, int c, int i){
                color1 = one;
                color2 = two;
                color3 = three;
                cluster = c;
                index = i;
            }
            public int getC1(){ return color1; }
            public int getC2(){ return color2; }
            public int getC3(){ return color3; }
            public int getCluster(){ return cluster; }
            public ColorsClusterIndex setCluster(int in){
                cluster = in;
                return this;
            }
            public int getIndex(){
                return index;
            }
            public int getSum(){
                return color1 + color2 + color3;
            }
        }

        Dimension imageInDimension = getImageDimension(imageIn);
        Random r = new Random();
        int pixel[] = imageToPixelsArray(imageIn);
        int My_row  =(int)imageInDimension.getHeight();   // row number
        int My_column =(int)imageInDimension.getWidth();  // column number
        int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        ArrayList<ColorsClusterIndex>[] kArray = new ArrayList[numSegments];
     //   ArrayList<ColorsClusterIndex>[] SIMArray = new ArrayList[numSegments];
        /*
        for(int i = 1; i<=3; i++){
            for(int row = 0 ; row<My_row; row++){
                for(int column = 0; column< My_column; column++){
                    System.out.print(TRGB[i][column][row] + "  ");
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();
        }*/



        for(int i=0; i<pixel.length; i++)
        {
            int c1, c2, c3;
                c1 = getRedComponent(pixel[i]);
                c2 = getGreenComponent(pixel[i]);
                c3 = getBlueComponent(pixel[i]);
            int rand = r.nextInt(numSegments);
            if(kArray[rand] == null)
                kArray[rand] = new ArrayList<ColorsClusterIndex>();
            ColorsClusterIndex temp = new ColorsClusterIndex(c1, c2, c3, rand, i);
            kArray[rand].add(temp);
         //   SIMArray[rand].add(temp);
        }

        boolean changes = true;
        double[][] means = new double[numSegments][3];
       // int flag = 0;
        while (changes)
        {
            changes = false;
            int sumC1, sumC2, sumC3;

            for(int i = 0; i<numSegments; i++){
                sumC1 = 0; sumC2 =0; sumC3 = 0;
                if(kArray[i] != null){
                    for(int j = 0; j < kArray[i].size(); j++){
                        sumC1 += kArray[i].get(j).getC1();
                        sumC2 += kArray[i].get(j).getC2();
                        sumC3 += kArray[i].get(j).getC3();
                    }
                means[i][0] = (double)sumC1/(double)kArray[i].size();
                means[i][1] = (double)sumC2/(double)kArray[i].size();
                means[i][2] = (double)sumC3/(double)kArray[i].size();
                }
            }

            ArrayList<ColorsClusterIndex>[] tempArray = new ArrayList[numSegments];
            for(int i = 0; i<numSegments; i++){
                if(kArray[i] != null){
                    int size = kArray[i].size()-1;
                    for(int j = size; j >= 0; j--){

                        double minAveDiff = Float.MAX_VALUE,result = 0;
                        int index = -1;
                       for(int k = 0; k<numSegments; k++){
                            int sumDiff = 0;
                            sumDiff += Math.pow((kArray[i].get(j).getC1()-means[k][0]),2);
                            sumDiff += Math.pow((kArray[i].get(j).getC2()-means[k][1]),2);
                            sumDiff += Math.pow((kArray[i].get(j).getC3()-means[k][2]),2);
                            if(sumDiff< minAveDiff){
                                minAveDiff = sumDiff;
                                index = k;
                            }
                        }

                        if(index != kArray[i].get(j).getCluster()){
                             changes = true;

                        }
                        if(tempArray[index] == null)
                            tempArray[index] = new ArrayList<ColorsClusterIndex>();
                        tempArray[index].add(kArray[i].get(j).setCluster(index));
                        kArray[i].remove(j);
                    }
                }
            }
            kArray = tempArray;
        }

          ArrayList<ColorsClusterIndex>[] tempArray = new ArrayList[numSegments];
            for(int i = 0; i<numSegments; i++){
                if(kArray[i] != null){
                    int size = kArray[i].size()-1;
                    for(int j = size; j >= 0; j--){

                        double minAveDiff = Float.MAX_VALUE,result = 0;
                        int index = -1;
                       for(int k = 0; k<numSegments; k++){
                          int sumDiff = 0;
                       double s1 = 0, s2 = 0, s3 = 0, Result = 0, downS1, downS2,downS3;
                       double p1;
                       if(kArray[i].get(j).getC1() == 0 ){
                           p1 = 1.0/(255+255+255);
                       }else{
                           p1 = (double)(kArray[i].get(j).getC1()+1)/(kArray[i].get(j).getSum());
                       }

                       double q1;
                       if(means[k][0] == 0){
                          q1 = 1.0/(255+255+255);
                       }else{
                           q1 = (means[k][0]+1)/(means[k][0]+means[k][1]+means[k][2]+1);
                       }
                        s1 = Math.abs(p1*Math.log(p1/q1));
                        downS1 = Math.abs(q1*Math.log(q1/p1));

                        double p2;
                       if(kArray[i].get(j).getC2()==0){
                           p2 = 1.0/(255+255+255);
                       }else{
                           p2 = (double)(kArray[i].get(j).getC2())/(kArray[i].get(j).getSum());
                       }
                        double q2;
                        if(means[k][1]==0){
                           q2 = 1.0/(255+255+255);
                        }else{
                           q2 = (means[k][1])/(means[k][0]+means[k][1]+means[k][2]);
                        }
                        s2 = Math.abs(p2*Math.log(p2/q2));
                        downS2 = Math.abs(q2*Math.log(q2/p2));

                        double p3;
                        if(kArray[i].get(j).getC3()==0){
                            p3 = 1.0/(255+255+255);
                        }else{
                            p3 = (double)(kArray[i].get(j).getC3())/(kArray[i].get(j).getSum());
                       }
                        double q3;
                        if(means[k][2]==0){
                            q3 = 1.0/(255+255+255);
                        }else{
                           q3 = (double)(means[k][2])/(means[k][0]+means[k][1]+means[k][2]);
                        }
                        s3 = Math.abs(p3*Math.log(p3/q3));
                       downS3 = Math.abs(q3*Math.log(q3/p3));
                       Result = (s1 + s2 + s3 + downS1 +downS2 + downS3)*100000000;
                            if(Result< minAveDiff){
                                minAveDiff = Result;
                                index = k;
                            }
                        }

                        if(index != kArray[i].get(j).getCluster()){
                             changes = true;

                        }
                        if(tempArray[index] == null)
                            tempArray[index] = new ArrayList<ColorsClusterIndex>();
                        tempArray[index].add(kArray[i].get(j).setCluster(index));
                        kArray[i].remove(j);
                    }
                }
            }
         kArray = tempArray;

        for(int i = 0; i<numSegments; i++)
        {
          int rd,gr,bl;
                if(i==0){
                    rd = 255; gr = 0; bl = 0;}
                else if(i==1){
                    rd = 0; gr = 255; bl = 0;}
                else if(i==2){
                    rd = 0; gr = 0; bl = 255;}
                else if(i==3){
                    rd = 255; gr = 255; bl = 255;}
                else if(i==4){
                    rd = 0; gr = 0; bl = 0;}
                else if(i==5){
                    rd = 255; gr = 255; bl = 0;}
                else if(i==6){
                    rd = 0; gr = 255; bl = 255;}
                else if(i==7){
                    rd = 255; gr = 0; bl = 255;}
                else{
                    rd = r.nextInt(256);
                    gr = r.nextInt(256);
                    bl = r.nextInt(256);}


            if(kArray[i] != null){
                for(int j = 0; j<kArray[i].size(); j++)
                {
                    pixel[kArray[i].get(j).getIndex()] = getTRGB(255,rd,gr,bl);
                }
            }
        }
         for(int i = 0; i < numSegments; i++){
             System.out.println(" "+ i +" R = " + means[i][0] + " G = " +means[i][1] + "B = " + means[i][2]);
         }


        return pixelsArrayToImage(pixel, imageInDimension);
    }



    protected Image possibilityCMeansSegmentation(Image imageIn, int numSegments, double fuzzyness, double epsilon){
        Dimension imageInDimension = getImageDimension(imageIn);
        Random rand = new Random();
        double[][] means = new double[numSegments][3];
        double [] eta = new double[numSegments];
        int pixel[] = imageToPixelsArray(imageIn);
        double [][] u = new double[numSegments][pixel.length];
        double[] sum = new double[pixel.length];
        boolean notDoneYet = true;
        double [][] t = new double[numSegments][pixel.length];
        double [][] closestMean = new double[pixel.length][2];

        //Fuzzy C-Means
        //generating random membership values
        for(int i = 0; i<numSegments; i++){
            for(int j = 0; j<pixel.length; j++){
                double r = rand.nextDouble();
                sum[j] += r;
                u[i][j] = r;
            }
        }

        //making it so the membership values for a pixel  sum to 1
        for(int i = 0; i<numSegments; i++){
            for(int j = 0; j<pixel.length; j++){
                u[i][j] = u[i][j]/sum[j];
            }
        }

        //loop until none of the cluster means change greater than epsilon between iterations
        while(notDoneYet){
            //caluculating each cluster's mean from the membership values.
            for(int i = 0; i< numSegments; i++){
                double sumWeight = 0, sumR =0, sumG =0, sumB =0;
                double weight;
                for(int j = 0; j<pixel.length; j++){
                    weight = Math.pow(u[i][j], fuzzyness);
                    sumWeight += weight;
                    sumR += weight*getRedComponent(pixel[j]);
                    sumG += weight*getGreenComponent(pixel[j]);
                    sumB += weight*getBlueComponent(pixel[j]);
                }

                double meanR = sumR/sumWeight;
                double meanG = sumG/sumWeight;
                double meanB = sumB/sumWeight;

                //deciding if the stop condition has been met
                double euclidDist = Math.sqrt(Math.pow(meanR-means[i][0], 2)+Math.pow(meanG-means[i][1], 2)+Math.pow(meanB-means[i][2], 2));
                if(euclidDist<epsilon)
                    notDoneYet = false;

                means[i][0] = meanR;
                means[i][1] = meanG;
                means[i][2] = meanB;
            }

            //updating the membership values
            for(int i = 0; i<numSegments; i++){
                for(int j = 0; j<pixel.length; j++){
                    double distSqTop = Math.sqrt(Math.pow(getRedComponent(pixel[j])-means[i][0], 2) + Math.pow(getGreenComponent(pixel[j])-means[i][1], 2) + Math.pow(getBlueComponent(pixel[j])-means[i][2], 2));
                    double uInverse = 0;
                    for (int h = 0; h<numSegments; h++){
                        double distSqBottom = Math.sqrt(Math.pow((getRedComponent(pixel[j])-means[h][0]), 2) + Math.pow((getGreenComponent(pixel[j])-means[h][1]), 2) + Math.pow((getBlueComponent(pixel[j])-means[h][2]), 2));
                        uInverse += Math.pow((distSqTop/distSqBottom),(2/(fuzzyness-1)));
                    }

                    u[i][j] = Math.pow(uInverse, -1);
                }
            }
        }

        //Possiblity C-Means
        //calculating the initial values of eta with the results from fuzzy C-means
        for(int i=0; i<numSegments; i++){
            double topSum = 0, bottomSum = 0;
            for(int j = 0; j<pixel.length; j++){
                double distToMean = Math.pow((getRedComponent(pixel[j])-means[i][0]),2) + Math.pow((getGreenComponent(pixel[j])-means[i][1]),2) +
                                                                        Math.pow((getBlueComponent(pixel[j])-means[i][2]),2);
                topSum += Math.pow(u[i][j], fuzzyness) * distToMean;
                bottomSum += Math.pow(u[i][j], fuzzyness);
            }
            eta[i] = topSum/bottomSum;
        }

        //loop until none of the cluster means change greater than epsilon between iterations
        notDoneYet = true;
        while(notDoneYet){
            for(int i=0; i<numSegments; i++){
                for(int j = 0; j<pixel.length; j++){
                    t[i][j] = 1/(1 + Math.pow((Math.pow((getRedComponent(pixel[j])-means[i][0]),2)+Math.pow((getGreenComponent(pixel[j])-means[i][1]),2)+ Math.pow((getBlueComponent(pixel[j])-means[i][2]),2))/eta[i], 1/(fuzzyness-1)));
                }
            }

            for(int i = 0; i< numSegments; i++){
                    double sumWeight = 0, sumR =0, sumG =0, sumB =0;
                    double weight;
                    for(int j = 0; j<pixel.length; j++)
                    {
                        weight = Math.pow(t[i][j], fuzzyness);
                        sumWeight += weight;
                        sumR += weight*getRedComponent(pixel[j]);
                        sumG += weight*getGreenComponent(pixel[j]);
                        sumB += weight*getBlueComponent(pixel[j]);
                    }

                    double meanR = sumR/sumWeight;
                    double meanG = sumG/sumWeight;
                    double meanB = sumB/sumWeight;

                    double euclidDist = Math.sqrt(Math.pow(meanR-means[i][0], 2)+Math.pow(meanG-means[i][1], 2)+Math.pow(meanB-means[i][2], 2));
                    if(euclidDist<epsilon)
                        notDoneYet = false;

                    means[i][0] = meanR;
                    means[i][1] = meanG;
                    means[i][2] = meanB;
            }


            for(int i=0; i<pixel.length; i++){
                closestMean[i][0] = Double.MAX_VALUE;
            }

            for(int i=0; i<numSegments; i++){
                double topSum = 0, bottomSum = 0;
                for(int j = 0; j<pixel.length; j++){
                    double distToMean = Math.sqrt(Math.pow((getRedComponent(pixel[j])-means[i][0]),2) +
                            Math.pow((getGreenComponent(pixel[j])-means[i][1]),2) + Math.pow((getBlueComponent(pixel[j])-means[i][2]),2));
                    topSum += Math.pow(t[i][j], fuzzyness) * distToMean;
                    bottomSum += Math.pow(t[i][j], fuzzyness);

                    if(distToMean<closestMean[j][0]){
                        closestMean[j][0] = distToMean;
                        closestMean[j][1] = i;
                    }
                }
                eta[i] = topSum/bottomSum;
            }
        }

        int[][] colorValues = getColorValues(numSegments);

        for(int i = 0; i< pixel.length; i++){
            pixel[i] = getTRGB(colorValues[(int)closestMean[i][1]][0], colorValues[(int)closestMean[i][1]][1],
                    colorValues[(int)closestMean[i][1]][2], colorValues[(int)closestMean[i][1]][3]);
        }

        return pixelsArrayToImage(pixel, imageInDimension);
    }

   protected Image MyMorphologyErosion(Image imageIn){
        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int ReturnTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int[][]  x = {{0,0,0},{0,0,0},{0,0,0}};
        int row  =(int)imageInDimension.getHeight();   // row number
        int column =(int)imageInDimension.getWidth();  // column number
        for (int r = 0; r < row; r++){
            for (int c = 0; c < column; c++){
		int value = (TRGB[1][c][r] + TRGB[2][c][r] + TRGB[3][c][r]) / 3;
                if (value > 255)
                    value = 255;
                else if (value < 0)
                        value =0;
                TRGB[1][c][r] = value;
		TRGB[2][c][r] = value;
		TRGB[3][c][r] = value;
            }
        }
         for(int R = 1; R < row-1; R++){
             for(int C = 1; C< column-1 ; C++){
                 int temp = 0;
                 float min = Float.MAX_VALUE;
                 for(int i = -1; i <=1; i++){
                     for(int j = -1; j<=1; j++){
                         temp = TRGB[1][C+j][R+i]+x[i+1][j+1];
                         if(temp < min)
                             min = temp;
                     }
                 }
                 ReturnTRGB[1][C][R] = (int)min;
                 ReturnTRGB[2][C][R] = (int)min;
                 ReturnTRGB[3][C][R] = (int)min;
             }
         }
        return pixelsArrayToImage(TRGBArrayToPixelsArray(ReturnTRGB, imageInDimension), imageInDimension);
    }

    protected Image MyMorphologyDilation(Image imageIn){
        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int ReturnTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int[][]  x = {{0,0,0},{0,0,0},{0,0,0}};
        int row  =(int)imageInDimension.getHeight();   // row number
        int column =(int)imageInDimension.getWidth();  // column number
        for (int r = 0; r < row; r++){
            for (int c = 0; c < column; c++){
		int average = (TRGB[1][c][r] + TRGB[2][c][r] + TRGB[3][c][r]) / 3;
                if (average > 255)
                    average = 255;
                else if (average < 0)
                        average =0;
                TRGB[1][c][r] = average;
		TRGB[2][c][r] = average;
		TRGB[3][c][r] = average;
            }
        }
         for(int R = 1; R < row-1; R++){
             for(int C = 1; C< column-1 ; C++){
                 int temp = 0;
                 float max = -1;
                 for(int i = -1; i <=1; i++){
                     for(int j = -1; j<=1; j++){
                         temp = TRGB[1][C+j][R+i]+x[i+1][j+1];
                         if(temp > max)
                             max = temp;
                     }
                 }
                 ReturnTRGB[1][C][R] = (int)max;
                 ReturnTRGB[2][C][R] = (int)max;
                 ReturnTRGB[3][C][R] = (int)max;
             }
         }
        return pixelsArrayToImage(TRGBArrayToPixelsArray(ReturnTRGB, imageInDimension), imageInDimension);
    }
     protected Image MyMorphologyOpen(Image imageIn){
        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int TRGB2[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int ReturnTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int[][]  x = {{0,0,0},{0,0,0},{0,0,0}};
        int row  =(int)imageInDimension.getHeight();   // row number
        int column =(int)imageInDimension.getWidth();  // column number
        for (int r = 0; r < row; r++){
            for (int c = 0; c < column; c++){
		int average = (TRGB[1][c][r] + TRGB[2][c][r] + TRGB[3][c][r]) / 3;
                if (average > 255)
                    average = 255;
                else if (average < 0)
                        average =0;
                TRGB[1][c][r] = average;
		TRGB[2][c][r] = average;
		TRGB[3][c][r] = average;
            }
        }
          for(int R = 1; R < row-1; R++){
             for(int C = 1; C< column-1 ; C++){
                 int temp = 0;
                 float min = Float.MAX_VALUE;
                 for(int i = -1; i <=1; i++){
                     for(int j = -1; j<=1; j++){
                         temp = TRGB[1][C+j][R+i]+x[i+1][j+1];
                         if(temp < min)
                             min = temp;
                     }
                 }
                 TRGB2[1][C][R] = (int)min;
                 TRGB2[2][C][R] = (int)min;
                 TRGB2[3][C][R] = (int)min;
             }
         }
         for(int R = 1; R < row-1; R++){
             for(int C = 1; C< column-1 ; C++){
                 int temp = 0;
                 float max = -1;
                 for(int i = -1; i <=1; i++){
                     for(int j = -1; j<=1; j++){
                         temp = TRGB2[1][C+j][R+i]+x[i+1][j+1];
                         if(temp > max)
                             max = temp;
                     }
                 }
                 ReturnTRGB[1][C][R] = (int)max;
                 ReturnTRGB[2][C][R] = (int)max;
                 ReturnTRGB[3][C][R] = (int)max;
             }
         }
        return pixelsArrayToImage(TRGBArrayToPixelsArray(ReturnTRGB, imageInDimension), imageInDimension);
    }
     protected Image MyMorphologyClose(Image imageIn){
        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int TRGB2[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int ReturnTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int[][]  x = {{0,0,0},{0,0,0},{0,0,0}};
        int row  =(int)imageInDimension.getHeight();   // row number
        int column =(int)imageInDimension.getWidth();  // column number
        for (int r = 0; r < row; r++){
            for (int c = 0; c < column; c++){
		int average = (TRGB[1][c][r] + TRGB[2][c][r] + TRGB[3][c][r]) / 3;
                if (average > 255)
                    average = 255;
                else if (average < 0)
                        average =0;
                TRGB[1][c][r] = average;
		TRGB[2][c][r] = average;
		TRGB[3][c][r] = average;
            }
        }
        for(int R = 1; R < row-1; R++){
             for(int C = 1; C< column-1 ; C++){
                 int temp = 0;
                 float max = -1;
                 for(int i = -1; i <=1; i++){
                     for(int j = -1; j<=1; j++){
                         temp = TRGB[1][C+j][R+i]+x[i+1][j+1];
                         if(temp > max)
                             max = temp;
                     }
                 }
                 TRGB2[1][C][R] = (int)max;
                 TRGB2[2][C][R] = (int)max;
                 TRGB2[3][C][R] = (int)max;
             }
         }
          for(int R = 1; R < row-1; R++){
             for(int C = 1; C< column-1 ; C++){
                 int temp = 0;
                 float min = Float.MAX_VALUE;
                 for(int i = -1; i <=1; i++){
                     for(int j = -1; j<=1; j++){
                         temp = TRGB2[1][C+j][R+i]+x[i+1][j+1];
                         if(temp < min)
                             min = temp;
                     }
                 }
                 ReturnTRGB[1][C][R] = (int)min;
                 ReturnTRGB[2][C][R] = (int)min;
                 ReturnTRGB[3][C][R] = (int)min;
             }
         }

        return pixelsArrayToImage(TRGBArrayToPixelsArray(ReturnTRGB, imageInDimension), imageInDimension);
    }
         protected Image MyHSItoRGB(Image imageIn)
      {
         Dimension imageInDimension = getImageDimension(imageIn);
         int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
         int altTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);


         for (int row = 0; row < imageInDimension.getHeight(); row++)
            for (int column = 0; column < imageInDimension.getWidth(); column++)
            {

               double h = (TRGB[1][column][row]) * Math.PI / 127.5;
               double s = TRGB[2][column][row] / 255.0;
               double i = TRGB[3][column][row] / 255.0;

               double r = 0, g = 0, b = 0;
               if(h < 2 * Math.PI /3)
               {
                  double x = i * (1 - s);
                  double y = i * (1 + (s * Math.cos(h) / Math.cos(Math.PI / 3 - h)));
                  double z = 3 * i - (x + y);
                  b = x;
                  r = y;
                  g = z;
               }
               else if(h < 4 * Math.PI /3)
               {
                  h -= 2 * Math.PI /3;
                  double x = i * (1 - s);
                  double y = i * (1 + (s * Math.cos(h) / Math.cos(Math.PI / 3 - h)));
                  double z = 3 * i - (x + y);
                  r = x;
                  g = y;
                  b = z;
               }
               else if(h < 2 * Math.PI)
               {
                  h -= 4 * Math.PI /3;
                  double x = i * (1 - s);
                  double y = i * (1 + (s * Math.cos(h) / Math.cos(Math.PI / 3 - h)));
                  double z = 3 * i - (x + y);
                  g = x;
                  b = y;
                  r = z;
               }

               int R = (int)(r * 255);
               int G = (int)(g * 255);
               int B = (int)(b * 255);

               altTRGB[1][column][row] = R;
               altTRGB[2][column][row] = G;
               altTRGB[3][column][row] = B;
            }


         return pixelsArrayToImage(TRGBArrayToPixelsArray(altTRGB, imageInDimension), imageInDimension);

      }
        protected Image MyRGBtoHSI_H(Image imageIn)      {
         Dimension imageInDimension = getImageDimension(imageIn);
         int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
         int altTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);


         for (int row = 0; row < imageInDimension.getHeight(); row++)
            for (int column = 0; column < imageInDimension.getWidth(); column++)
            {
               int H = 0;
               int S = 0;
               int I = 0;
               if((TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row])==0)
               {
                  ;
               }
               else
               {
                  double r = 1.0 * TRGB[1][column][row] / (TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row]);
                  double g = 1.0 * TRGB[2][column][row] / (TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row]);
                  double b = 1.0 * TRGB[3][column][row] / (TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row]);
                  double h = 0, s = 0, i = 0;
                  //get h
                  if(b <= g)
                     h = Math.acos(0.5 * ((r - g)+(r - b)) / Math.pow((Math.pow((r - g), 2) + (r - b) * (g - b)), 0.5));
                  else
                     h = 2 * Math.PI - Math.acos(0.5 * ((r - g)+(r - b)) / Math.pow((Math.pow((r - g), 2) + (r - b) * (g - b)), 0.5));

               	//get s
                  s = 1 - 3 *((((r < g)? r: g) < b)?((r < g)? r: g): b);

               	//get i
                  i = (TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row]) / (3.0 * 255);

                  H = (int)((h * 127.5 / Math.PI));
                  S = (int)(s * 255);
                  I = (int)(i * 255);
               }
               altTRGB[1][column][row] = H;
               altTRGB[2][column][row] = S;
               altTRGB[3][column][row] = I;

            }


         return pixelsArrayToImage(TRGBArrayToPixelsArray(altTRGB, imageInDimension), imageInDimension);

      }
    protected Image MyRGBtoHSI_S(Image imageIn){
	Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        //int ReturnTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int pixel[] = imageToPixelsArray(imageIn);
        int My_row  =(int)imageInDimension.getHeight();   // row number
        int My_column =(int)imageInDimension.getWidth();  // column number
         int i = 0;
        //  make the color image to gray and caculate number in each pixel
        for (int row = 0; row < My_row; row++){
            for (int column = 0; column < My_column; column++){
                float r = 0, g = 0, b =0;
                r = (float)TRGB[1][column][row]/(TRGB[1][column][row]+TRGB[2][column][row]+TRGB[3][column][row]) ;
		g = (float)TRGB[2][column][row]/(TRGB[1][column][row]+TRGB[2][column][row]+TRGB[3][column][row]) ;
		b = (float)TRGB[3][column][row]/(TRGB[1][column][row]+TRGB[2][column][row]+TRGB[3][column][row]) ;
                double top =0, down =0, h =0;
              if(b <= g) {
                  top = 0.5*( 2*r-g-b );
                  down =Math.pow((r-g), 2)+(r-b)*(g-b);
                  h = Math.acos(top/Math.pow(down,0.5));
              }
               else
                 if(b > g){
                   top = 0.5*( 2*r-g-b );
                  down =Math.pow((r-g), 2)+(r-b)*(g-b);
                  h = 2*Math.PI - Math.acos(top/Math.pow(down,0.5));
              }
                int H = (int)(h*180.0/Math.PI);
                int S = (int)(100*(1-3*(Math.min(b, Math.min(r, g)))));
                int I = (int)((TRGB[1][column][row] + TRGB[2][column][row] +TRGB[3][column][row])/3.0);
                int R = (int)(S*255.0/100.0);
                pixel[i] = getTRGB(255,R,R,R);
                 i++;

            }
        }

        return pixelsArrayToImage(pixel, imageInDimension);

	//return pixelsArrayToImage(TRGBArrayToPixelsArray(ReturnTRGB, imageInDimension), imageInDimension);
    }
        protected Image MyRGBtoHSI_I(Image imageIn){
	Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        //int ReturnTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int pixel[] = imageToPixelsArray(imageIn);
        int My_row  =(int)imageInDimension.getHeight();   // row number
        int My_column =(int)imageInDimension.getWidth();  // column number
         int i = 0;
        //  make the color image to gray and caculate number in each pixel
        for (int row = 0; row < My_row; row++){
            for (int column = 0; column < My_column; column++){
                float r = 0, g = 0, b =0;
                r = (float)TRGB[1][column][row]/(TRGB[1][column][row]+TRGB[2][column][row]+TRGB[3][column][row]) ;
		g = (float)TRGB[2][column][row]/(TRGB[1][column][row]+TRGB[2][column][row]+TRGB[3][column][row]) ;
		b = (float)TRGB[3][column][row]/(TRGB[1][column][row]+TRGB[2][column][row]+TRGB[3][column][row]) ;
                double top =0, down =0, h =0;
              if(b <= g) {
                  top = 0.5*( 2*r-g-b );
                  down =Math.pow((r-g), 2)+(r-b)*(g-b);
                  h = Math.acos(top/Math.pow(down,0.5));
              }
               else
                 if(b > g){
                   top = 0.5*( 2*r-g-b );
                  down =Math.pow((r-g), 2)+(r-b)*(g-b);
                  h = 2*Math.PI - Math.acos(top/Math.pow(down,0.5));
              }
                int H = (int)(h*180.0/Math.PI);
                int S = (int)(100*(1-3*(Math.min(b, Math.min(r, g)))));
                int I = (int)((TRGB[1][column][row] + TRGB[2][column][row] +TRGB[3][column][row])/3.0);
                pixel[i] = getTRGB(255,I,I,I);
                 i++;

            }
        }

        return pixelsArrayToImage(pixel, imageInDimension);

	//return pixelsArrayToImage(TRGBArrayToPixelsArray(ReturnTRGB, imageInDimension), imageInDimension);
    }
    protected Image My_contrastStretch(Image imageIn){
	Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int My_row  =(int)imageInDimension.getHeight();   // row number
        int My_column =(int)imageInDimension.getWidth();  // column number
        int sLow = (int) My_row*My_column/100;         // slow = 10%
        int sHight= (int) My_row*My_column*90/100;     // shight = 90 %
        int aLow = 0;
        int aHight = 255;
        int h[] = new int[256]; // install histogram
        //  make the color image to gray and caculate number in each pixel
        for (int row = 0; row < My_row; row++){
            for (int column = 0; column < My_column; column++){
		int average = (TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row]) / 3;
                if (average > 255)
                    average = 255;
                else if (average < 0)
                        average =0;
                TRGB[1][column][row] = average;
		TRGB[2][column][row] = average;
		TRGB[3][column][row] = average;
                h[average]++;
            }
        }
        // cumulative histogram
      for (int i = 0; i<= 254; i++){
          h[i+1] = h[i] +h[i+1];
      }
        // formula: alow =  min{i| h[i] >= M*N*Slow}
      for(int i = 0; i <= 255; i++){
        if (h[i] >= sLow){
              aLow = i;break;
        }
      }
        // formula : ahight = max {i| h[i] <= M*N**(1-Shight)}
        for(int i =255; i>=0; i--){
        if (h[i]<= sHight){
            aHight = i;break;
        }
       }
    // linear contrast enhancement
        for(int i =0; i< My_row; i++){
            for(int j = 0; j< My_column; j++){
             if (TRGB[1][j][i] <= aLow)
                TRGB[1][j][i] = 0;
             else if (TRGB[1][j][i] >= aHight)
                 TRGB[1][j][i] = 255;
             else
                 TRGB[1][j][i] = (TRGB[1][j][i] - aLow)*255/(aHight-aLow);
            }
        }
        // assigh TRGB[2] and TRGB[3]
           for(int r = 0; r< My_row; r++){
            for(int c=0; c<My_column; c++){
        TRGB[2][c][r] = TRGB[1][c][r];
        TRGB[3][c][r] = TRGB[1][c][r];
            }
        }
	return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);
    }
      protected Image MyKmeansSegmentation(Image imageIn, int k){
         class ClusterIndex{
            private int R, G, B, cluster, index_x,index_y;
            public ClusterIndex(int red, int Green, int Blue, int c, int x, int y){
                R = red;
                G = Green;
                B = Blue;
                cluster = c;
                index_x = x;
                index_y = y;
            }
            public int getR(){ return R; }
            public int getG(){ return G; }
            public int getB(){ return B; }
            public int getCluster(){ return cluster; }
            public ClusterIndex setCluster(int in){
                cluster = in;
                return this;
            }
            public int getIndex_x(){
                return index_x;
            }
            public int getIndex_y(){
                return index_y;
            }
        }

        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        Random r = new Random();
        int My_row  =(int)imageInDimension.getHeight();   // row number
        int My_column =(int)imageInDimension.getWidth();  // column number
        ArrayList<ClusterIndex>[] kArray = new ArrayList[k];
        double[][] numberCluster = new double[k][3];
      int[] compare = new int[k];
       for(int row = 1; row <= (int)(My_row); row++){
           for(int colum =1; colum<= (int)(My_column); colum++){
                        for(int seg = 0; seg < k; seg++){
                       if(kArray[(colum*row)%k] == null)
                             kArray[(colum*row)%k] = new ArrayList<ClusterIndex>();
                             ClusterIndex temp = new ClusterIndex(TRGB[1][colum-1][row-1], TRGB[2][colum-1][row-1], TRGB[3][colum-1][row-1], seg, row-1,colum-1);
                       kArray[(colum*row)%k].add(temp);
                //       compare[(colum*row)%k]++;

           }
      }
    }

     boolean Flag = true;
        ArrayList<ClusterIndex>[] TArray = new ArrayList[k];
        int Max_Loop = 3;
        int M =0;
        int[] compare2 = new int[k];

        while(Flag)
        {
            M++;
            if(M ==Max_Loop){
                break;
            }
            Flag = false;

                    for(int i = 0; i< k; i++){

             int s1, s2, s3;
                if(kArray[i] != null){
                    s1 = 0; s2 =0; s3 = 0;
                    for(int j = 0; j < kArray[i].size(); j++){
                        s1 += kArray[i].get(j).getR();
                        s2 += kArray[i].get(j).getG();
                        s3 += kArray[i].get(j).getB();
                    }

                numberCluster[i][0] = (double)s1/(double)kArray[i].size();
                numberCluster[i][1] = (double)s2/(double)kArray[i].size();
                numberCluster[i][2] = (double)s3/(double)kArray[i].size();
                }
         }
          for(int i = 0; i< k; i++){
                if(kArray[i] != null){
                    int size = kArray[i].size()-1;
                    for(int j = size; j >= 0; j--){

                        double Min = Float.MAX_VALUE;
                        int index = -1;
                        for(int seg = 0; seg< k; seg++){
                            int sum = 0;
                            sum += Math.pow((kArray[i].get(j).getR()-numberCluster[seg][0]),2);
                            sum += Math.pow((kArray[i].get(j).getG()-numberCluster[seg][1]),2);
                            sum += Math.pow((kArray[i].get(j).getB()-numberCluster[seg][2]),2);
                            if(sum< Min){
                                Min = sum;
                                index = seg;
                            }
                        }
                        if(index != kArray[i].get(j).getCluster())
                            Flag = true;
                        if(TArray[index] == null)
                            TArray[index] = new ArrayList<ClusterIndex>();
                        TArray[index].add(kArray[i].get(j).setCluster(index));
                        kArray[i].remove(j);
                        compare2[index] ++;
                    }
                }
            }

            kArray = TArray;
            int x = 0;
            for(int j= 0; j <k; j++){

                if (compare2[j] == compare[j]){
                    x++ ;
                }

            if (x == k){
                Flag = false ;
            }
            else
                compare = compare2;

            }


        }



        for(int i = 0; i< k; i++)
        {
          int red,gree, blue;
          if(i==0){
              red = 255; gree = 0; blue = 0;}
          else if(i==1){
              red = 0; gree = 0; blue = 255;}
          else if(i==2){
              red = 0; gree = 255; blue = 0;}
          else if(i==3){
              red = 255; gree = 255; blue = 255;}
          else if(i==4){
              red = 0; gree = 0; blue = 0;}
          else{
               red = r.nextInt(256);
               gree = r.nextInt(256);
               blue = r.nextInt(256);}
            if(kArray[i] != null){
                for(int j = 0; j<kArray[i].size(); j++)
                {
                    TRGB[1][kArray[i].get(j).getIndex_y()][kArray[i].get(j).getIndex_x()] = red;
                    TRGB[2][kArray[i].get(j).getIndex_y()][kArray[i].get(j).getIndex_x()] = gree;
                    TRGB[3][kArray[i].get(j).getIndex_y()][kArray[i].get(j).getIndex_x()] = blue;
                }
            }
        }


        return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);
    }
    //input parameters : input image and number of clusters
    protected Image MyACO(Image imageIn, int k){
        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int TRGB2[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        Random r = new Random();
        int My_row  =(int)imageInDimension.getHeight();   // row number
        int My_column =(int)imageInDimension.getWidth();  // column number
       class cluster{
           public double Rcenter;
           public double Gcenter;
           public double Bcenter;
           public double center;

           public int XpCent;
           public int YpCent;
           public double physDist;
           public double AvgPhysDist;

           public double siRDist;
           public double siGDist;
           public double siBDist;
           public double AvgSiRDist;
           public double AvgSiGDist;
           public double AvgSiBDist;
           public double siTotalDist;

           public int PrvXpCent;
           public int PrvYpCent;
           public int XSum;
           public int YSum;

           public double RPrvCenter;
           public double GPrvCenter;
           public double BPrvCenter;
           public double Rsum;
           public double Gsum;
           public double Bsum;
           public int[][] member;
           public double colorDist;
           public double AvgColorDist;

           public double[][] T;
           public double[][] N;
           public double[][] P;
           public double pix;
           public double[][] DeltaT;
           cluster(int column, int row){
                member = new int[column][row];
                T = new double[column][row];
                P = new double[column][row];
                N = new double[column][row];
                DeltaT = new double[column][row];
           }
       }

      class ant{
           public cluster[] cc = new cluster[10];
           public double[] Arr = new double[10];
           public double min;
           public double maxdist;
           public double maxdist2;
           public double maxdist3;
           public double maxPhysdist;
           public double maxPhysdist2;
           public double maxPhysdist3;
           public double maxSidist;
           public double maxSidist2;
           public double maxSidist3;
           public int vote;
           ant(int column, int row){
              for(int i = 0; i < 7 ; i++){
                    cc[i] = new cluster(column,row);
              }
           }
       }
      //initializing "M" the number of ants, # of Iterationsbbv
       cluster[] clusCenter = new cluster[7];
       ant[]  a = new ant[10];
       double[] Y = new double[10];
       double[] copy = new double[10];
       double alpha = 2;
       double beta = 5;
       double kapa = 1000.0;
       int Q = 10;
       int m = 7;
       int iteration = 6;
       double p = 0.8;
       int NumOfClusters = k;
       int[] redcolor = new int[7];
       int[] greencolor = new int[7];
       int[] bluecolor = new int[7];

       redcolor[0] = 255;       greencolor[0] = 255;         bluecolor[0] = 255;
       redcolor[1] = 0;        greencolor[1] = 0;         bluecolor[1] = 0;
       redcolor[2] = 250;         greencolor[2] = 250;           bluecolor[2] = 50;
       redcolor[3] = 100;       greencolor[3] = 255;         bluecolor[3] = 255;
       redcolor[4] = 0;       greencolor[4] = 255;         bluecolor[4] = 255;
       redcolor[5] = 255;       greencolor[5] = 0;         bluecolor[5] = 255;
       redcolor[6] = 0;         greencolor[6] = 250;         bluecolor[6] = 0;

       int comment = 0;

       int finalant = 0;
      for(int i = 0; i < NumOfClusters ; i++){
          clusCenter[i] = new cluster(My_column,My_row);
      }

      for(int mm = 1 ; mm <= m ; mm++ ){
       //   for(int i = 0; i < NumOfClusters ; i++){
              a[mm] = new ant(My_column,My_row);
         // }
      }
      //ACO-Kmeans
      //Initializing pheromone level
       for(int i = 0; i < NumOfClusters ; i++){
           for(int row = 0 ; row < My_row ; row++){
               for(int column = 0 ; column < My_column ; column++){
                   clusCenter[i].T[column][row] = 1;
               }
           }
       }


      for(int t = 1; t<= iteration ; t++){           // iteration
          for(int count = 1; count <= m;  count++){        // k is the number of the ants
              // creating k different random cluster centers
              if(t == 1){
                 for(int i = 0 ; i < NumOfClusters ; i++ ){
                       int row = r.nextInt(My_row);
                       int colum = r.nextInt(My_column);
                       clusCenter[i].Rcenter = TRGB[1][colum][row];
                       clusCenter[i].Gcenter = TRGB[2][colum][row];
                       clusCenter[i].Bcenter = TRGB[3][colum][row];

               //        System.out.println(i + "t = 1, R = " + clusCenter[i].Rcenter);
               //        System.out.println("t = 1, G = " + clusCenter[i].Gcenter);
               //        System.out.println("t = 1, B = " + clusCenter[i].Bcenter);

                 }
              }// if (t == 1)

              // heuristic information
           int e =1;
            int num = 0;
            while (e == 1)// repeat until ant k has completed a solution
            {

            for(int i = 0; i < NumOfClusters; i++){
                for(int row = 0 ; row < My_row; row++){
                    for(int column = 0; column < My_column; column++){
                        int red = TRGB[1][column][row];
                        int green = TRGB[2][column][row];
                        int blue = TRGB[3][column][row];
                        double a1 = (red - clusCenter[i].Rcenter);
                        double b1 = (green - clusCenter[i].Gcenter);
                        double c1 = (blue - clusCenter[i].Bcenter);
                        if((a1 == 0)&&(b1 == 0)&&(c1 == 0)){
                            clusCenter[i].N[column][row] = 1000000;
                        }else{
                            double moce = kapa;
                            double temp = Math.pow(a1,2) + Math.pow(b1,2) + Math.pow(c1, 2);
                            double deno = Math.pow(temp, 0.5);
                            clusCenter[i].N[column][row] =  moce/deno;
                        }
                   //    System.out.println(clusCenter[i].N[column][row]);
                    }
                }
            }
/////////////////////////////////////////////////////////////////////////////////////////////////////////
              Y[0] = 0;

                  double sum;
                  for(int row = 0 ; row < My_row; row++){
                      for(int column = 0 ; column < My_column; column++){
                          sum = 0;
                          for(int i = 0; i < NumOfClusters ; i++){
                              double first = Math.exp((Math.log(clusCenter[i].T[column][row]))*alpha);
                              double second = Math.exp((Math.log(clusCenter[i].N[column][row]))*beta);
                              sum += first*second;
                          }

                          for(int i = 0; i < NumOfClusters ; i++){
                              double first = Math.exp((Math.log(clusCenter[i].T[column][row]))*alpha);
                              double second = Math.exp((Math.log(clusCenter[i].N[column][row]))*beta);
                              clusCenter[i].P[column][row] = (first*second)/sum;
                          }
                      }
                  }
                  // clustering according to the prbabilities calculated abouve
                  // first, we define a set of numbers sorted from 0 to 1 as Y[]
                  // so that Y[i] - Y[i -1] = clusCenter[i].P[x]
                  double tttp = -1;
                  double pppp = 0;
                  int index = -1;
                  for(int row = 0; row < My_row; row++){
                      for(int column = 0 ; column < My_column ; column++){
                          for(int ii = 1 ; ii <= NumOfClusters; ii++){
                              Y[ii] = clusCenter[ii - 1].P[column][row] + Y[ii-1];
                              pppp = clusCenter[ii - 1].P[column][row];
                              clusCenter[ii - 1].member[column][row] = 0;
                           //   System.out.println(" Y  " + Y[ii]);
                              if(Math.abs(pppp) > tttp){
                                    tttp = pppp;
                                    index = ii - 1;
                              }

                          }
                    //     clusCenter[index].member[column][row] = 1;

                           double rr = r.nextInt(100000)/100000.0;
                           /* rr is a random floating point value in the range[0,1)(inlcuding 0 ,not inlcuding 1)
                            * Note we numst convert rand() and/or RAND_Max + 1 to floating point values to avoid integer division.
                            * In addition, sean scanlon pointed out the possbility
                            * that Random_max may be the largest positive integer the architecture can represent, so (Rand_max +1) may result in an overflow,
                            * or more likely the value will end up being the largest negative interger the architecture can represent, so to avoid this we
                            * convert Rand_max and 1 to double before adding.
                            */
                         for(int i = 1; i <= NumOfClusters; i++ ){   // loop for comparing n intervals
                                                                     // j: index of compared interval
                                if(rr >= Y[i - 1] && rr < Y[i]){
                               //     System.out.println("memeber cluster " + (i - 1));
                                  clusCenter[i-1].member[column][row] = 1;
                                break;
                               }
                          }
                       //

                     }
                  }
                  // calculating cluster centers
                  for(int ii = 0; ii < NumOfClusters; ii++ ){
                      clusCenter[ii].XSum = 0;
                      clusCenter[ii].YSum = 0;
                      clusCenter[ii].Rsum = 0;
                      clusCenter[ii].Gsum = 0;
                      clusCenter[ii].Bsum = 0;
                      clusCenter[ii].pix = 1;
                  }
                 for(int i = 0; i < NumOfClusters; i++){
                     for(int row = 0; row < My_row; row++){
                         for(int column = 0; column < My_column; column++){
                             double Xp = row;
                             double Yp = column;
                             if(clusCenter[i].member[column][row] == 1){
                                  int red = TRGB[1][column][row];
                                  int green = TRGB[2][column][row];
                                  int blue = TRGB[3][column][row];

                                 clusCenter[i].XSum += Xp;
                                 clusCenter[i].YSum += Yp;

                                 clusCenter[i].Rsum += red;
                                 clusCenter[i].Gsum += green;
                                 clusCenter[i].Bsum += blue;
                                 clusCenter[i].pix ++;


                             }
                         }
                     }
                //     System.out.println(i + "cluscenter" +clusCenter[i].Rsum );
                 }

                  e = 10;
                  num++;
                  if(num == 50){
                      break;
                  }
                   for(int i = 0; i < NumOfClusters; i++){

                       clusCenter[i].RPrvCenter = clusCenter[i].Rcenter;
                       clusCenter[i].GPrvCenter = clusCenter[i].Gcenter;
                       clusCenter[i].BPrvCenter = clusCenter[i].Bcenter;

                       clusCenter[i].Rcenter = (double)(clusCenter[i].Rsum)/clusCenter[i].pix;
                       clusCenter[i].Gcenter = (double)(clusCenter[i].Gsum)/clusCenter[i].pix;
                       clusCenter[i].Bcenter = (double)(clusCenter[i].Bsum)/clusCenter[i].pix;

                       clusCenter[i].XpCent = (int)(clusCenter[i].XSum/clusCenter[i].pix);
                       clusCenter[i].YpCent = (int)(clusCenter[i].YSum/clusCenter[i].pix);

                       double temp = Math.pow(clusCenter[i].Rcenter,2) +
                                     Math.pow(clusCenter[i].Gcenter,2) +
                                     Math.pow(clusCenter[i].Bcenter,2);
                       clusCenter[i].center = Math.pow(temp, 0.5);

                       double E1 = Math.abs( clusCenter[i].RPrvCenter - clusCenter[i].Rcenter);
                       double E2 = Math.abs( clusCenter[i].GPrvCenter - clusCenter[i].Gcenter);
                       double E3 = Math.abs( clusCenter[i].BPrvCenter - clusCenter[i].Bcenter);
                       if((E1 > 1)||(E2 > 1)||(E3 > 1)){
                           e =1;
                       }
              //       System.out.println("rcenter = " + clusCenter[i].Rcenter);
             //        System.out.println("gcenter = " + clusCenter[i].Gcenter);
             //        System.out.println("bcenter = " + clusCenter[i].Bcenter);
                   }


              }//end while ant k has completed a soulition

            // assign the clustering properties to the ant
              for(int i = 0 ; i < NumOfClusters ; i++){
                  a[count].cc[i].Rcenter = clusCenter[i].Rcenter;
                  a[count].cc[i].Gcenter = clusCenter[i].Gcenter;
                  a[count].cc[i].Bcenter = clusCenter[i].Bcenter;

                  a[count].cc[i].XpCent = clusCenter[i].XpCent;
                  a[count].cc[i].YpCent = clusCenter[i].YpCent;
                  a[count].cc[i].center = clusCenter[i].center;

                  a[count].cc[i].Rsum = clusCenter[i].Rsum;
                  a[count].cc[i].Gsum = clusCenter[i].Gsum;
                  a[count].cc[i].Bsum = clusCenter[i].Bsum;

                  a[count].cc[i].pix = clusCenter[i].pix;

                  System.out.println(i + " R center " + a[count].cc[i].Rcenter);
                  System.out.println(i + " G center " + a[count].cc[i].Gcenter);
                  System.out.println(i + " B center " + a[count].cc[i].Bcenter);

                  for(int row = 0 ; row < My_row ; row++ ){
                      for(int column = 0 ; column < My_column ; column ++ ){
                          a[count].cc[i].member[column][row] = clusCenter[i].member[column][row];
                      }
                  }
              }

            // finding the minimum color distance between every two cluster centers
              double temp = Math.pow(a[count].cc[0].Rcenter - a[count].cc[1].Rcenter,2)
                          + Math.pow(a[count].cc[0].Gcenter - a[count].cc[1].Gcenter,2)
                          + Math.pow(a[count].cc[0].Bcenter - a[count].cc[1].Bcenter,2);
              a[count].min = Math.pow(temp, 0.5);

              for(int i = 0; i < NumOfClusters-1 ; i++){
                  for(int j = i+1 ; j < NumOfClusters ; j++ ){
                     double temp2 = Math.pow(a[count].cc[0].Rcenter - a[count].cc[1].Rcenter,2)
                                 + Math.pow(a[count].cc[0].Gcenter - a[count].cc[1].Gcenter,2)
                                 + Math.pow(a[count].cc[0].Bcenter - a[count].cc[1].Bcenter,2);
                     if(Math.pow(temp2, 0.5) < a[count].min){
                         a[count].min = Math.pow(temp2, 0.5);
                     }

                  }
              }
              // calculating sum of the solor and physical distance and si distance between each cluster cetner and all its memter
              // pixels
              for(int i = 0; i < NumOfClusters ; i++){
                  a[count].cc[i].colorDist = 0;
                  a[count].cc[i].physDist = 0;

                  a[count].cc[i].siRDist = 0;
                  a[count].cc[i].siGDist = 0;
                  a[count].cc[i].siBDist = 0;

              }

              for(int i = 0; i < NumOfClusters ; i++ ){
                  for(int row = 0; row < My_row ;  row++){
                      for(int column = 0; column < My_column ; column++){
                          if(a[count].cc[i].member[column][row] == 1){
                              int ro = row;
                              int co = column;

                              int red = TRGB[1][co][ro];
                              int green = TRGB[2][co][ro];
                              int blue = TRGB[3][co][ro];

                              double t2 = Math.pow((double)(red - a[count].cc[i].Rcenter), 2)
                                          + Math.pow(green - a[count].cc[i].Gcenter , 2)
                                          + Math.pow(blue - a[count].cc[i].Bcenter, 2);
                              double t3 = Math.pow((double)(ro - a[count].cc[i].XpCent),2)
                                        + Math.pow(co - a[count].cc[i].YpCent , 2);
                              a[count].cc[i].colorDist += Math.pow(t2, 0.5);
                              a[count].cc[i].physDist  += Math.pow(t3, 0.5);

                              a[count].cc[i].siRDist += red;
                              a[count].cc[i].siGDist += green;
                              a[count].cc[i].siBDist += blue;
                          }
                      }
                  }
              }
              for(int i = 0; i < NumOfClusters ; i++){
                  a[count].cc[i].AvgColorDist = (double)(a[count].cc[i].colorDist)/a[count].cc[i].pix ;
                  a[count].cc[i].AvgPhysDist = (int)((double)(a[count].cc[i].physDist)/a[count].cc[i].pix);
                  a[count].cc[i].AvgSiBDist = (double)(a[count].cc[i].siBDist)/a[count].cc[i].pix ;
                  a[count].cc[i].AvgSiGDist = (double)(a[count].cc[i].siGDist)/a[count].cc[i].pix ;
                  a[count].cc[i].AvgSiRDist = (double)(a[count].cc[i].siRDist)/a[count].cc[i].pix ;
              }

              for(int row = 0 ; row < My_row ; row++){
                  for(int column = 0 ; column < My_column ; column++){
                      double minAveDiff = Float.MAX_VALUE ;
                      int index = -1;
                      for(int j = 0; j < NumOfClusters ; j++){
                          if(a[count].cc[j].member[column][row] == 1){
                              double s1 = 0, s2 = 0, s3 = 0, result = 0,downS1, downS2, downS3 ;
                              double p1;
                              if(TRGB[1][column][row] == 0){
                                  p1 = 1.0/(255+255+255);
                              }else{
                                  p1 = (double)(TRGB[1][column][row])/(TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row]);
                              }

                              double q1;
                              if(a[count].cc[j].siRDist == 0){
                                  q1 = 1.0/(255+255+255);
                              }else{
                                  q1 = a[count].cc[j].siRDist/(a[count].cc[j].siRDist + a[count].cc[j].siGDist + a[count].cc[j].siBDist);
                              }
                              s1 = Math.abs(p1*(Math.log(p1/q1)/Math.log(2.0)));
                              downS1 = Math.abs(q1*(Math.log(q1/p1)/Math.log(2.0)));

                              double p2;
                              if(TRGB[2][column][row]==0){
                                  p2 = 1.0/(255+255+255);
                              }else{
                                  p2 = (double)(TRGB[2][column][row])/(TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row]);
                              }

                              double q2;
                              if(a[count].cc[j].siGDist == 0){
                                  q2 = 1.0/(255+255+255);
                              }else{
                                  q2 = a[count].cc[j].siGDist/(a[count].cc[j].siRDist + a[count].cc[j].siGDist + a[count].cc[j].siBDist);
                              }
                              s2 = Math.abs(p2*(Math.log(p2/q2)/Math.log(2.0)));
                              downS2 = Math.abs(q2*(Math.log(q2/p2)/Math.log(2.0)));

                              double p3;
                              if(TRGB[3][column][row]==0){
                                  p3 = 1.0/(255+255+255);
                              }else{
                                  p3 = (double)(TRGB[3][column][row])/(TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row]);
                              }

                              double q3;
                              if(a[count].cc[j].siBDist == 0){
                                  q3 = 1.0/(255+255+255);
                              }else{
                                  q3 = a[count].cc[j].siBDist/(a[count].cc[j].siRDist + a[count].cc[j].siGDist + a[count].cc[j].siBDist);
                              }
                              s3 = Math.abs(p3*(Math.log(p3/q3)/Math.log(2.0)));
                              downS3 = Math.abs(q3*(Math.log(q3/p3)/Math.log(2.0)));

                              double sum = (TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row]);
                              double sum2 = a[count].cc[j].siRDist + a[count].cc[j].siGDist + a[count].cc[j].siBDist;
                              double con = Math.abs(sum - sum2);
                              con = Math.pow(con,2);
                              if(con == 0){
                                  con =1;
                              }
                              result = Math.pow((s1+s2+s3+downS1+downS2+downS3),2);
                              result = con*result*10000 ;
                              a[count].cc[j].siTotalDist += result;
                          }//end if
                      }// end column
                  }//end row
              }//end row

              // finding the maximum of the color distance between each cluster  center and all its member pixels
              for(int i  = 0; i < NumOfClusters ; i++){
                   a[count].Arr[i] = a[count].cc[i].colorDist;
              }

              temp = 0;
              a[count].maxdist = a[count].cc[0].colorDist;

              for(int i = 0; i< NumOfClusters ; i++){
                  if(a[count].cc[i].colorDist > a[count].maxdist){
                      a[count].maxdist = a[count].cc[i].colorDist;
                      temp = i;
                  }
              }

              double temp2 = a[count].Arr[0];
              a[count].Arr[0] = a[count].Arr[(int)(temp)];
              a[count].Arr[(int)(temp)] = (double)(temp2);

              temp = 1;
              a[count].maxdist2 = a[count].Arr[1];
              for(int i  = 1 ; i < NumOfClusters ; i++){
                  if(a[count].Arr[i] > a[count].maxdist2){
                      a[count].maxdist2 = a[count].Arr[i];
                      temp = i;
                  }
              }
              if(NumOfClusters > 2){
                   temp2 = a[count].Arr[1];
                  a[count].Arr[1] = a[count].Arr[(int)(temp)];
                  a[count].Arr[(int)(temp)] = (double)(temp2);

                  temp = 2;
                  for(int i = 2; i < NumOfClusters; i++){
                      if(a[count].Arr[i] > a[count].maxdist3){
                          a[count].maxdist3 = a[count].Arr[i];
                          temp = i;
                      }
                  }
              }
////////////////////////////////////////
              // finding the maximum of the physical distance between each clusters center and all its member pixels
              for(int i =0; i < NumOfClusters ; i++){
                  a[count].Arr[i] = a[count].cc[i].physDist;
              }

              temp = 0;
              a[count].maxPhysdist = a[count].cc[0].physDist ;
              for(int i = 0; i < NumOfClusters ; i++){
                  if(a[count].cc[i].physDist > a[count].maxPhysdist){
                      a[count].maxPhysdist = a[count].cc[i].physDist;
                      temp = i;
                  }
              }

              double t1 = a[count].Arr[0];
              a[count].Arr[0] = a[count].Arr[(int)(temp)];
              a[count].Arr[(int)(temp)] = t1 ;

              temp = 1;
              a[count].maxPhysdist2 = a[count].Arr[1];
              for(int i = 1; i < NumOfClusters ; i++){
                  if(a[count].Arr[i] > a[count].maxPhysdist2){
                      a[count].maxPhysdist2 = a[count].Arr[i];
                      temp = i;
                  }
              }
              if(NumOfClusters > 2){
                  temp2 = a[count].Arr[1];
                  a[count].Arr[1] = a[count].Arr[(int)(temp)];
                  a[count].Arr[(int)(temp)] = temp2;

                  temp = 2;
                  a[count].maxPhysdist3 = a[count].Arr[2];
                  for(int i = 2 ; i < NumOfClusters ; i++){
                      if(a[count].Arr[i] > a[count].maxPhysdist3){
                          a[count].maxPhysdist3 = a[count].Arr[i];
                          temp = i;
                      }
                  }
              }
////////////////////////////////////////////////
              // find the maximum of the si distance between each cluster center and all its member pixels
              for(int i = 0; i < NumOfClusters ; i++){
                  a[count].Arr[i] = a[count].cc[i].siTotalDist ;
              }
              temp = 0;
              a[count].maxSidist = a[count].cc[0].siTotalDist ;
              for(int i = 0; i < NumOfClusters ; i++){
                  if(a[count].cc[i].siTotalDist > a[count].maxSidist){
                      a[count].maxSidist = a[count].cc[i].siTotalDist;
                      temp = i;
                  }
              }

              t1 = a[count].Arr[0];
              a[count].Arr[0] = a[count].Arr[(int)(temp)];
              a[count].Arr[(int)(temp)] = t1;

              temp = 1;
              a[count].maxSidist2 = a[count].Arr[1];
              for(int i = 1; i < NumOfClusters; i++){
                  if(a[count].Arr[i] > a[count].maxSidist2){
                      a[count].maxSidist2 = a[count].Arr[i];
                      temp = i;
                  }
              }
              if(NumOfClusters > 2){
                  double t2 = a[count].Arr[1];
                  a[count].Arr[1] = a[count].Arr[(int)(temp)];
                  a[count].Arr[(int)(temp)] = t2;

                  temp = 2;
                  a[count].maxSidist3 = a[count].Arr[2];
                  for(int i = 2; i < NumOfClusters ; i++ ){
                      if(a[count].Arr[i] > a[count].maxSidist3){
                          a[count].maxSidist3 = a[count].Arr[i];
                          temp = i;
                      }
                  }
              }

            System.out.println("System count" + count);
          }// end for(int k = 0; k< NumOfClusters ; k++)
         // save the best solution so far
          double max = a[1].min;
          double min = a[1].maxdist;
          double min2 = a[1].maxdist2;
          double min3 = a[1].maxdist3;
          double Pmin = a[1].maxPhysdist;
          double Pmin2 = a[1].maxPhysdist2;
          double pmin3 = a[1].maxPhysdist3;

          double Smin = a[1].maxSidist;
          double Smin2 = a[1].maxSidist2;
          double Smin3 = a[1].maxSidist3;

          int temp = 1;
          for(int i = 1; i < m ; i++){ // i is the number of ants
              a[i].vote = 0;
          }

          temp = 1;
          int temp1 = 0;
          min = 0;
          for(int i = 1; i <= m ; i++ ){// i is the number of ants
              if(a[i].maxdist < min){
                  min = a[i].maxdist;
                  temp = i;              // and temp is providing a bettwer solution
              }
          }
          a[(int)(temp)].vote++;
          if(comment == 0 ){  // vote for the second best twoo
              if(temp == m){
                  temp1 = 1;
              }else{
                  temp1 = (int)(temp +1);
              }
              min = a[temp1].maxdist;

              for(int i  = 1; i<= m ; i++){         // is is the number of the ants
                  if(i != (int)(temp) && a[i].maxdist < min){
                      min = a[i].maxdist;
                      temp1 = i;
                  }
              }
              a[temp1].vote ++;
          }

          temp = 1;
          for(int i = 1; i<= m ; i++){// k is the number of the ants
              if(a[i].min > max){
                  max = a[i].min;
                  temp = i;   /// ant temp is providing a better solution
              }
          }
          a[(int)(temp)].vote ++;

          if(comment == 0){// vote for the second best too.
              if(temp == m){
                  temp1 = 1;
              }else{
                  temp1 = (int)(temp) +1;
              }
              max =a[temp1].min;
              for(int i = 1 ; i<= m; i++){  // i is the number of the ants
                  if(i != (int)(temp) && a[i].min > max){
                      max = a[i].min;
                      temp1 = i; // ant temp is providing a better solution
                  }
              }
              a[temp1].vote++;
          }

          temp = 1;
          for(int i = 1; i <= m ; i++){ // i is the number of the ants
              if(a[i].maxdist2 < min2){
                  min2 = a[i].maxdist2;
                  temp = i;// ant temp is providing a better solution
              }
          }
          a[(int)(temp)].vote ++;
          if(comment == 0){ // vote for the second best too
              if(temp == m){
                  temp1 = 1;
              }else{
                  temp1 = temp + 1;
              }
              min2 = a[temp1].maxdist2;
              for(int i = 1; i <= m ; i++){  // i is the number of the ants
                  if(i != temp && a[i].maxPhysdist < min2){
                      min2 = a[i].maxdist2;
                      temp1 = i; // ant temp is providing a better solution
                  }
              }
              a[temp1].vote++;
          }
          temp = 1;
          for(int i = 1; i <= m; i++){
              if(a[i].maxdist3 < min3){
                  min3 = a[i].maxdist3;
                  temp = i;
              }
          }
          a[(int)(temp)].vote++;
          if(comment == 0){
              if(temp == m){
                  temp1 = 1;
              }else{
                  temp1 = temp + 1;
              }
              min3 = a[temp1].maxdist3;
              for(int i = 1; i <= m; i++){
                  if( i != temp && a[i].maxdist3 < min3 ){
                      min3 = a[i].maxdist3;
                      temp1 = i;
                  }
              }
              a[temp1].vote ++;
          }
          temp = 1;
          for(int i = 1; i <= m ; i++){ // i is the number of the ants
              if(a[i].maxSidist < Smin){
                  Smin = a[i].maxSidist;
                  temp = i;// ant temp is providing a better solution
              }
          }
          a[(int)(temp)].vote ++;
          if(comment == 0){// vote for the second best too
              if(temp == m){
                  temp1 = 1;
              }else{
                  temp1 = temp +1;
              }
              Smin2 = a[temp1].maxSidist2;
              for(int i = 1; i <= m ; i++){// k is the number of the ants
                  if(i != temp && a[i].maxSidist2 < Smin2){
                      Smin2 = a[i].maxSidist2;
                      temp1 = i;// ant temp is providing a better solution
                  }
              }
              a[temp1].vote ++;
          }
          temp = 1;
          for(int i = 1; i <= m ; i++){
              if(a[i].maxSidist3< Smin3){
                  Smin3 = a[i].maxSidist3;
                  temp = i; // ant temp is providing a better solution
              }
          }
          a[(int)(temp)].vote ++;
          if(comment == 0){
              if(temp == m){
                  temp1 = 1;
              }else{
                  temp1 = temp + 1;
              }
              Smin3 = a[temp1].maxSidist3;
              for(int i =1; i <= m ; i++){
                  if(i != temp && a[i].maxSidist3 < Smin3){
                      Smin3 = a[i].maxSidist3;
                      temp1 = i;
                  }
              }
              a[temp1].vote++;
          }

          max = a[1].vote;
          temp = 1;
          for(int i = 1; i <= m ; i++){
              if(a[i].vote > max){
                  max = a[i].vote;
                  temp = i;
              }
          }

          // update the pheromone level
          for(int i = 0; i < NumOfClusters ; i ++){
              for(int row = 0; row < My_row; row++){
                  for(int column = 0; column < My_column ; column++){
                      clusCenter[i].DeltaT[column][row] = 0;
                      if(a[temp].cc[i].member[column][row] == 1){
                          clusCenter[i].DeltaT[column][row] +=
                                  (double)(Q*a[(int)(temp)].min)/((a[(int)(temp)].cc[i].AvgColorDist));
                      }
                  }
              }
          }
          for(int i = 0 ; i < NumOfClusters; i ++){
              for(int row = 0; row < My_row; row++){
                  for(int column = 0; column < My_column ; column ++){
                      clusCenter[i].T[column][row] = (1-p)*clusCenter[i].T[column][row] + clusCenter[i].DeltaT[column][row];
                  }
              }
          }

         // update cluster centers

       //   for(int i = 0; i < NumOfClusters ; i ++){
       //       clusCenter[i].Rcenter = a[(int)(temp)].cc[i].Rcenter;
      //        clusCenter[i].Gcenter = a[(int)(temp)].cc[i].Gcenter;
      //        clusCenter[i].Bcenter = a[(int)(temp)].cc[i].Bcenter;
     //         clusCenter[i].center = a[(int)(temp)].cc[i].center;

           /////////////////////////////////////////////////////////////////////////////////////////////////////
           //   System.out.println("recenter " + clusCenter[i].Rcenter);
           //   System.out.println("gecenter " + clusCenter[i].Gcenter);

       //   }


          for(int i = 0; i < NumOfClusters ; i++){
              copy[i] = a[(int)(temp)].cc[i].Rcenter;
          }
          for(int i = 0; i < NumOfClusters; i ++){
              for(int j = i+1; j < NumOfClusters ; j++){
                  if(copy[i] < copy[j]){
                      double temp3 = copy[i];
                      copy[i] = copy[j];
                      copy[j] = temp3;
                  }
              }
          }
         temp1 = 0;
          for(int i = 0; i < NumOfClusters; i++){
              for(int row = 0; row < My_row; row++){
                  for(int column = 0; column < My_column ; column ++){

                      if(a[(int)(temp)].cc[i].member[column][row] == 1){
                          for(int j = 0; j < NumOfClusters ; j++){
                              if(a[(int)(temp)].cc[i].Rcenter == copy[j]){
                                  temp1 = j;
                              }
                          }

                             TRGB2[1][column][row] = redcolor[temp1];
                             TRGB2[2][column][row] = greencolor[temp1];
                             TRGB2[3][column][row] = bluecolor[temp1];



                      }
                  }
              }
            }



          //for(int i )//////



      //System.out.println("gecenter " + a[(int)(temp)].cc[0].Gcenter);

       finalant = temp;
      System.out.println("System t t t "+t);
      }//end termination
       System.out.println("System final ant "+finalant);
       for(int j = finalant; j <= 5 ; j++){
           System.out.println(" ant number " + j);
       for(int i = 0; i < NumOfClusters ; i++){
           System.out.println(" cluster " + i);
           System.out.println(a[j].cc[i].Rcenter);
           System.out.println(a[j].cc[i].Gcenter);
           System.out.println(a[j].cc[i].Bcenter);
       }
       }

        return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB2, imageInDimension), imageInDimension);
        //return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);
    }


    //////////////////////////////////////////////////////////////
      protected Image MyOptimalthredshold(Image imageIn){

        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int r  =(int)imageInDimension.getHeight();
        int c =(int)imageInDimension.getWidth();
        int threshole = 200;

        int[] h =  new int[256];
        float[] p = new float[256];
        float min = Float.MAX_VALUE;
        int Fthreshold = 0;
            for (int row = 0; row < r; row++){
            for (int column = 0; column < c; column++){
		int average = (TRGB[1][column][row] + TRGB[2][column][row] + TRGB[3][column][row]) / 3;
                if (average > 255)
                    average = 255;
                else if (average < 0)
                        average =0;
                TRGB[1][column][row] = average;
		TRGB[2][column][row] = average;
		TRGB[3][column][row] = average;
                h[average] ++;
            }
        }
        for(int i = 0;i< 256; i++ ){
                  p[i] = (float)h[i]/(r*c);

        }
        boolean flag = true;
        float u2 = 0;
        for(int i = 0; i < 256; i++ ){
                u2 += i*h[i];
            }
        u2 = u2/(r*c);
        float v1 = 0;
        for(int i = 0; i< 256; i++){
               v1 += Math.pow((i-u2),2)*h[i];
        }
        v1 = v1/(r*c);
        while(flag){
            float w = 0,w2 = 0;
            float Ut = 0;
            for(int i = 0 ; i<= threshole ;i++ ){
                w += p[i];
                Ut += i*p[i]*h[i];
            }
            w2= 1 - w;
            double Vb = Math.pow(Ut*(u2 - Ut), 2)/(w*w2);
            double V = Vb/v1;

            if(min > V){
                min = (float)V;
                Fthreshold = threshole;
            }
            int  sum1 = 0, sum1s = 0 ;
            int sum2 = 0, sum2s = 0;

            for(int i = 0; i< 256 ; i++){
                if(i<= threshole){
                 sum1 += h[i];
                 sum1s += i*h[i];
                }
                else{
                sum2 += h[i];
                sum2s += i*h[i];
                }
            }
            int Temp = (int)(sum1s/(2.0*sum1) +sum2s/(2.0*sum2));
            if (Temp == threshole)
               flag = false;
            else
                threshole = Temp;
        };

          for (int row = 0; row < r; row++){
            for (int column = 0; column < c; column++){

               if(TRGB[1][column][row] >= Fthreshold ){
                TRGB[1][column][row] = 255;
		TRGB[2][column][row] = 255;
		TRGB[3][column][row] = 255;
               }
               else  if(TRGB[1][column][row] < Fthreshold ){
                TRGB[1][column][row] = 0;
		TRGB[2][column][row] = 0;
		TRGB[3][column][row] = 0;
               }
            }
        }
        return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);

  }
  protected Image doThinning(Image imageIn){

        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int TRGB1[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int rowV  =(int)imageInDimension.getHeight();
        int columnV =(int)imageInDimension.getWidth();



            for (int r = 0; r < rowV; r++){

             for (int c = 0; c < columnV; c++){
		int average = (TRGB[1][c][r] + TRGB[2][c][r] + TRGB[3][c][r]) / 3;
                if (average > 155)
                    average = 0;
                else if (average <=155)
                        average =1;
                TRGB[1][c][r] = average;
		TRGB[2][c][r] = average;
		TRGB[3][c][r] = average;
                TRGB1[1][c][r] = average;
		TRGB1[2][c][r] = average;
		TRGB1[3][c][r] = average;
            }
        }
       for(int flag = 1; flag <=2 ; flag++){
            for(int i = 1; i< rowV-1 ; i ++){
                for (int j = 1; j < columnV-1 ; j++){
                   // int temppping;
                    int[] temp = new int[10];
                    int sumBp = 0, Ap = 0;

                    temp[1] = TRGB[1][j][i];
                    temp[2] = TRGB[1][j][i-1];
                    temp[3] = TRGB[1][j+1][i-1];
                    temp[4] = TRGB[1][j+1][i];
                    temp[5] = TRGB[1][j+1][i+1];
                    temp[6] = TRGB[1][j][i+1];
                    temp[7] = TRGB[1][j-1][i+1];
                    temp[8] = TRGB[1][j-1][i];
                    temp[9] = TRGB[1][j-1][i-1];
                    temp[0] = TRGB[1][j][i-1];
                 for(int m = 3; m <= 10;m++){
                     if (m == 10){
                           if (temp[9]== 0 && temp[0]== 1)
                                    Ap++;
                      }else{
                           if(temp[m-1] == 0 && temp[m] == 1){
                                    Ap++;
                           }
                           }
                            if (m != 10){
                                sumBp += temp[m-1];
                            }
                }

                    if(flag == 1){
                         int temp1 = temp[2]*temp[4]*temp[6], temp2 = temp[8]*temp[4]*temp[6];

                        if(sumBp>=2 && sumBp<=6)
                                if(Ap==1)
                                    if((temp1+ temp2) == 0){
                                        TRGB1[1][j][i] = 0;/////////////////////
                                    }

                    }
                    if(flag == 2 ){
                        int temp1 = temp[2]*temp[4]*temp[8], temp2 = temp[8]*temp[2]*temp[6];

                        if(sumBp>=2 && sumBp<=6)
                                if(Ap==1)
                                    if((temp1+ temp2) == 0){
                                        TRGB1[1][j][i] =0;//////////////
                                    }
                    }
                    if(temp[1]==1){
                        if((temp[2]+temp[4]+temp[8]+temp[9] == 4)&&(temp[3]+temp[5]+temp[6]+temp[7]==0)){
                            TRGB1[1][j][i] = 1;
                        }
                        if((temp[8] == 1)&&(temp[2]+temp[3]+temp[4]+temp[5]+temp[6]+temp[7]+temp[9]==0)){
                            TRGB1[1][j][i] = 1;
                        }
                        if((temp[3]+temp[5]+temp[6]+temp[7]+temp[9] == 4)&&(temp[2]+temp[4]+temp[8] ==0)){
                            TRGB1[1][j][i] = 1;
                        }
                    }

                }

                }
            TRGB = TRGB1;
        }
         for (int r = 0; r < rowV; r++){
            for (int c = 0; c < columnV; c++){
                if(TRGB[1][c][r] == 0){
                    TRGB[1][c][r] = 255;
                    TRGB[2][c][r] = 255;
                    TRGB[3][c][r] = 255;
                }else{
                    TRGB[1][c][r] = 0;
                    TRGB[2][c][r] = 0;
                    TRGB[3][c][r] = 0;
                }

            }
         }

        return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);

  }

   protected Image MycornerDetection(Image imageIn){
	Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int Ix[][] = new int[(int)imageInDimension.getWidth()][(int)imageInDimension.getHeight()];
        int Iy[][] = new int[(int)imageInDimension.getWidth()][(int)imageInDimension.getHeight()];
        int result[][] = new int[(int)imageInDimension.getWidth()][(int)imageInDimension.getHeight()];
        double sum = 0;
        float max = -99;
        for (int r = 0; r < imageInDimension.getHeight(); r++)
            for (int c = 0; c < imageInDimension.getWidth(); c++){
		int average = (TRGB[1][c][r] + TRGB[2][c][r] + TRGB[3][c][r]) / 3;
                TRGB[1][c][r] = average;
		TRGB[2][c][r] = average;
		TRGB[3][c][r] = average;
	}
         for(int i = 0;  i<=imageInDimension.getHeight()-1; i++){
            for(int j = 0; j <= imageInDimension.getWidth()-1; j++ ){
                if(j == (imageInDimension.getWidth()-1)){
                    Ix[j][i] = TRGB[1][j][i];
                }else
                    if(j != imageInDimension.getWidth()-1){
                    Ix[j][i] = (TRGB[1][j][i] - TRGB[1][j+1][i]);
                }
                if(i==imageInDimension.getHeight()-1){
                    Iy[j][i] = TRGB[1][j][i];
                }else
                    if(i !=imageInDimension.getHeight()-1){
                        Iy[j][i] =(TRGB[1][j][i] - TRGB[1][j][i+1]);
                    }
             }
        }
        for(int i = 1;  i<=imageInDimension.getHeight()-2; i++){
            for(int j = 1; j <=imageInDimension.getWidth()- 2; j++ ){
                double[][] C= new double[2][2];
                double sumX = 0, sumY = 0, sumXY = 0;

                for(int m = 0; m <3; m++ ){
                    for(int n = 0; n<3; n++){
                        sumX += Math.pow(Ix[n+j-1][m+i-1],2);
                        sumY += Math.pow(Iy[n+j-1][m+i-1],2);
                        sumXY += Ix[n+j-1][m+i-1]*Iy[n+j-1][m+i-1];
                    }
                }
                C[0][0] = sumX;
                C[0][1] = sumXY;
                C[1][0] = sumXY;
                C[1][1] = sumY;
                double a = 1/100.0;
                double b = (sumX+sumY)/100.0;
                b = (-1)*b;
                double c = (sumX/10.0)*(sumY/10.0) - (sumXY/10.0)*(sumXY/10.0);
                double dt = Math.sqrt(Math.pow(b,2) - 4*a*c);

                if (dt <0){
                    result[j][i] = 0;
                    sum = sum + result[j][i];
                    continue;
                }
                int r1 = (int)((-1*b + dt)/(2*a));
                int r2 = (int)((-1*b - dt)/(2*a));
                result[j][i] = Math.min(r1,r2);
                sum += result[j][i];
                if(result[j][i] > max){
                    max = result[j][i];
                }
            }


    }
        int throshold = (int)sum/(int)((imageInDimension.getHeight()-3)*(imageInDimension.getWidth()-3));
        System.out.println(max);
        for(int i = 4;  i<=imageInDimension.getHeight()-4; i++){
            for(int j = 4; j <= imageInDimension.getWidth()-4; j++ ){
                if(result[j][i] > (double)100000){
                    for(int m = -3; m<= 3; m++){
                    TRGB[1][j+m][i] = 255;
                    TRGB[2][j+m][i] = 0;
                    TRGB[3][j+m][i] = 0;
                    TRGB[1][j][i+m] = 255;
                    TRGB[2][j][i+m] = 0;
                    TRGB[3][j][i+m] = 0;
                    }
                }
            }
        }
	return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);
    }

    //defind segmentation
  protected Image MyFuzzyCmeansSeg(Image imageIn, int numSegments)
    {

        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int My_row  = (int)imageInDimension.getHeight();   // row number
        int My_column =(int)imageInDimension.getWidth();  // column number
        Random rand = new Random();
        double [][][] u = new double[My_column][My_row][numSegments];
        int[][] means = new int[numSegments][3];
        int[][] means1 = new int[numSegments][3];
        for(int i = 0 ; i< numSegments; i ++){
           int rR = rand.nextInt(My_row);
           int rC = rand.nextInt(My_column);
           means [i][0] = TRGB [1][rC][rR];
           means [i][1] = TRGB [2][rC][rR];
           means [i][2] = TRGB [3][rC][rR];

      }

        boolean stop = true;
        while(stop){

            for(int i = 0; i<numSegments; i++){
                for(int j = 0; j< My_row; j++){
                    for(int x =0; x< My_column; x++){
                    double Top = Math.sqrt(Math.pow(TRGB[1][x][j]-means[i][0], 2)
                            + Math.pow(TRGB[2][x][j]-means[i][1], 2)
                            + Math.pow(TRGB[3][x][j]-means[i][2], 2));
                    double temp = 0;
                    if (Top == 0){
                        u[x][j][i] = 0 ;

                    }
                    else {
                    for (int h = 0; h<numSegments; h++){
                        double distSqBottom = Math.sqrt(Math.pow((TRGB[1][x][j]-means[h][0]), 2)
                                + Math.pow((TRGB[2][x][j]-means[h][1]), 2)
                                + Math.pow((TRGB[3][x][j]-means[h][2]), 2));
                        temp += Math.pow((Top/distSqBottom),2);
                         }
                    u[x][j][i] = Math.pow(temp, -1);
                    }
                 }
                }
            }

            for(int i = 0; i< numSegments; i++){
            means1[i][0] = means[i][0];
            means1[i][1] = means[i][1];
            means1[i][2] = means[i][2];
            }
           double sumWeight, sumC1, sumC2, sumC3;
           double weight;
           for(int i = 0; i< numSegments; i++){
                sumWeight = 0;
                sumC1 =0;
                sumC2 = 0;
                sumC3 = 0;

              //  System.out.println(sumWeight);
           for(int j = 0; j< My_row; j++){
                   for(int k = 0; k< My_column ; k++){
                    weight = Math.pow(u[k][j][i], 2);
                    sumWeight += weight;
                    sumC1 += weight*((double)TRGB[1][k][j]);
                    sumC2 += weight*((double)TRGB[2][k][j]);
                    sumC3 += weight*((double)TRGB[3][k][j]);

               //    System.out.println(" weight = "+ weight);
              //      System.out.print("R "+ TRGB[1][k][j]+ " " +"G= "+  " " + TRGB[2][k][j] + " B=  " + TRGB[3][k][j]);
                    }
             //      System.out.println();
                }
             //   System.out.println(" sumWeight = " + sumWeight);
             //   System.out.println(" sumC1 = " + sumC1);
                double mR = sumC1/sumWeight;
                double mG = sumC2/sumWeight;
                double mB = sumC3/sumWeight;
              //  System.out.println(meanRRRR);
                means[i][0] = (int)mR;
                means[i][1] = (int)mG;
                means[i][2] = (int)mB;

            }
         int x = 0;
          for(int i = 0; i < numSegments; i++ ){
                if((means[i][0]==means1[i][0])&&(means[i][1]==means1[i][1])&&(means[i][2]==means1[i][2])){
                    x++;
                }
            }
           if( x == numSegments)
               stop = false;
        }

        int red = 0 ,gree = 0,blue = 0;
        for(int i = 0; i< My_row; i++){
            for(int j = 0; j< My_column; j++){
                int index = -1;
                int Max = (int)(u[j][i][0]*10);
                for(int k = 0; k<numSegments; k++){
                    if(Max <= (int)(u[j][i][k]*10)){
                        Max = (int)(u[j][i][k]*10);
                        index = k;
           //         System.out.println(" row  = " + j + " column = " + i + "u = "  + u[j][i][k] + "index = " + index);
                    }
                }
                Random r = new Random();

          if(index==0){
              red = 255; gree = 0; blue = 0;}
          else if(index==1){
              red = 0; gree = 0; blue = 255;}
          else if(index==2){
              red = 0; gree = 255; blue = 0;}
          else if(index==3){
              red = 255; gree = 255; blue = 255;}
          else if(index==4){
              red = 0; gree = 0; blue = 0;}
          else {
               red = r.nextInt(256);
               gree = r.nextInt(256);
               blue = r.nextInt(256);
          }

                    TRGB[1][j][i] = red;
                    TRGB[2][j][i] = gree;
                    TRGB[3][j][i] = blue;

            }
        }
       return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);
    }
    protected Image MyRobustFuzzyCmeansSeg(Image imageIn, int numSegments, double fuzz)
    {

        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int My_row  = (int)imageInDimension.getHeight();   // row number
        int My_column =(int)imageInDimension.getWidth();  // column number
        Random rand = new Random();
        double [][][] u = new double[My_column][My_row][numSegments];
        double [][][] u2 = new double[My_column][My_row][numSegments];
        int[][] means = new int[numSegments][3];
        int[][] means1 = new int[numSegments][3];

        int[][] position = {{-1,0},{1,0},{0,1},{0,-1}};

        double beta = 100;
        double fuzzness = fuzz;

        double euclidMin = 2.0;


        for(int i = 0 ; i< numSegments; i ++){
           int rR = rand.nextInt(My_row);
           int rC = rand.nextInt(My_column);
           means [i][0] = TRGB [1][rC][rR];
           means [i][1] = TRGB [2][rC][rR];
           means [i][2] = TRGB [3][rC][rR];

      }

        boolean stop = true;
        while(stop){
           // Caculate Ulm
            for(int i = 0; i<numSegments; i++){
                for(int j = 0; j< My_row; j++){
                    for(int x =0; x< My_column; x++){
                    double Top = Math.sqrt(Math.pow(TRGB[1][x][j]-means[i][0], 2)
                            + Math.pow(TRGB[2][x][j]-means[i][1], 2)
                            + Math.pow(TRGB[3][x][j]-means[i][2], 2));
                    double temp = 0;
                    if (Top == 0){
                        u[x][j][i] = 0 ;
                    }
                    else {
                    for (int h = 0; h<numSegments; h++){
                        double distSqBottom = Math.sqrt(Math.pow((TRGB[1][x][j]-means[h][0]), 2)
                                + Math.pow((TRGB[2][x][j]-means[h][1]), 2)
                                + Math.pow((TRGB[3][x][j]-means[h][2]), 2));
                        temp += Math.pow((Top/distSqBottom),2);
                         }
                    u[x][j][i] = Math.pow(temp, -1/(fuzzness-1));
                    }
                 }
                }
            }
            //Caculate Ujk
            for(int iseg = 0; iseg < numSegments; iseg++){
                for(int irow = 1; irow < My_row-1; irow++){
                    for(int icolumn = 1; icolumn < My_column-1; icolumn++){
                       double Top =  Math.sqrt(Math.pow(TRGB[1][icolumn][irow] - means[iseg][0],2) +
                               Math.pow(TRGB[2][icolumn][irow]-means[iseg][1], 2) +
                               Math.pow(TRGB[3][icolumn][irow] - means[iseg][2], 2));
                       double Top2 = 0;
                       for(int ipos = 0; ipos < 4;ipos++){
                           for(int iiseg = 0; iiseg< numSegments; iiseg ++){
                              if(iiseg != iseg){
                                  Top2 += Math.pow(u[icolumn + position[ipos][1]][irow + position[ipos][0]][iiseg],fuzzness);
                              }// end  if(iiseg != iseg)
                           }// end inner iiseg
                       }// end inner ipos
                     u2[icolumn][irow][iseg] = Math.pow((Top + beta*Top2),-2/(fuzzness-1));

                    }// end icolumn
                }// end irow
            }// end iseg

            for(int irow = 1 ; irow < My_row -1; irow++){
                for(int icolumn = 1; icolumn < My_column -1 ; icolumn ++){
                    double sum = 0;
                    for(int iseg = 0; iseg < numSegments ; iseg ++){
                        sum += u2[icolumn][irow][iseg];
                    }// end for(..iseg..)
                    for(int iiseg = 0 ; iiseg < numSegments ; iiseg ++){
                        u2[icolumn][irow][iiseg] = u2[icolumn][irow][iiseg]/sum;
                    }//end for(..iiseg..)
                }// end icolumn
            }// end irow

            for(int i = 0; i< numSegments; i++){
            means1[i][0] = means[i][0];
            means1[i][1] = means[i][1];
            means1[i][2] = means[i][2];
            }
           double sumWeight, sumC1, sumC2, sumC3;
           double weight;
           for(int i = 0; i< numSegments; i++){
                sumWeight = 0;
                sumC1 =0;
                sumC2 = 0;
                sumC3 = 0;

              //  System.out.println(sumWeight);
           for(int j = 1; j< My_row-1; j++){
                   for(int k = 0; k< My_column -1; k++){
                    weight = Math.pow(u2[k][j][i], 2);
                    sumWeight += weight;
                    sumC1 += weight*((double)TRGB[1][k][j]);
                    sumC2 += weight*((double)TRGB[2][k][j]);
                    sumC3 += weight*((double)TRGB[3][k][j]);

               //    System.out.println(" weight = "+ weight);
              //      System.out.print("R "+ TRGB[1][k][j]+ " " +"G= "+  " " + TRGB[2][k][j] + " B=  " + TRGB[3][k][j]);
                    }
             //      System.out.println();
                }
             //   System.out.println(" sumWeight = " + sumWeight);
             //   System.out.println(" sumC1 = " + sumC1);
                double mR = sumC1/sumWeight;
                double mG = sumC2/sumWeight;
                double mB = sumC3/sumWeight;
              //  System.out.println(meanRRRR);
                means[i][0] = (int)mR;
                means[i][1] = (int)mG;
                means[i][2] = (int)mB;

            }
         //int x = 0;
          for(int i = 0; i < numSegments; i++ ){
           double ss = Math.sqrt(Math.pow(means[i][0] - means1[i][0],2) +
                                 Math.pow(means[i][1] - means1[i][1],2) +
                                 Math.pow(means[i][2] - means1[i][2],2));
             if(ss < euclidMin){
                   stop = false;
             }
           }
           //if( x == numSegments){
           //    stop = false;
          // }
        }// end while

        int red = 0 ,gree = 0,blue = 0;
        for(int i = 1; i< My_row-1; i++){
            for(int j = 1; j< My_column-1; j++){
                int index = -1;
                int Max = (int)(u2[j][i][0]*100000);
                for(int k = 0; k<numSegments; k++){
                    if(Max <= (int)(u2[j][i][k]*100000)){
                        Max = (int)(u2[j][i][k]*100000);
                        index = k;
           //         System.out.println(" row  = " + j + " column = " + i + "u = "  + u[j][i][k] + "index = " + index);
                    }
                }
                Random r = new Random();

          if(index==0){
              red = 255; gree = 0; blue = 0;}
          else if(index==1){
              red = 0; gree = 255; blue = 0;}
          else if(index==2){
              red = 0; gree = 0; blue = 255;}
          else if(index==3){
              red = 255; gree = 255; blue = 255;}
          else if(index==4){
              red = 0; gree = 0; blue = 0;}
          else {
               red = r.nextInt(256);
               gree = r.nextInt(256);
               blue = r.nextInt(256);
          }

                    TRGB[1][j][i] = red;
                    TRGB[2][j][i] = gree;
                    TRGB[3][j][i] = blue;

            }
        }

        for(int i = 0; i < numSegments; i ++){
            System.out.println("first : R " + means[i][0] + " G" + means[i][1] + " B " + means[i][2]);
        }
       return pixelsArrayToImage(TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);
    }

    protected Image GradientSobel(Image imageIn){
        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int ReturnTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int[][]  x = {{-1,0,1},{-2,0,2},{-1,0,1}};
        int[][]  y = {{-1,-2,-1},{0,0,0},{1,2,1}};
        int row1  =(int)imageInDimension.getHeight();   // row number
        int col1 =(int)imageInDimension.getWidth();  // column number
        int SumX;
        int SumY;
        for(int demision =1; demision <= 3; demision++){
            for (int row = 1; row < row1-1; row++){
                 for (int column = 1; column < col1-1; column++){
                     SumX = 0;
                     SumY = 0;
		    for(int i = -1; i<=1; i++){
                        for(int j = -1; j<=1; j++){
                        SumX =SumX+ TRGB[demision][column +j][row +i]*x[i+1][j+1];
                        SumY =SumY+ TRGB[demision][column +j][row +i]*y[i+1][j+1];
                        }
                    }
                     ReturnTRGB[demision][column][row] = (int)Math.round(Math.sqrt(SumX*SumX+SumY*SumY));

              }
             }
        }

        return pixelsArrayToImage(TRGBArrayToPixelsArray(ReturnTRGB, imageInDimension), imageInDimension);
    }

        protected Image OrientationSobel(Image imageIn){
        Dimension imageInDimension = getImageDimension(imageIn);
	int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int ReturnTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int[][]  x = {{-1,0,1},{-2,0,2},{-1,0,1}};
        int[][]  y = {{-1,-2,-1},{0,0,0},{1,2,1}};
        int My_row  =(int)imageInDimension.getHeight();   // row number
        int My_column =(int)imageInDimension.getWidth();  // column number
        int SumX;
        int SumY;
        for(int demision =1; demision <= 3; demision++){
            for (int row = 1; row < My_row-1; row++){
                 for (int column = 1; column < My_column-1; column++){
                     SumX = 0;
                     SumY = 0;
		    for(int i = -1; i<=1; i++){
                        for(int j = -1; j<=1; j++){
                        SumX =SumX+ TRGB[demision][column +j][row +i]*x[i+1][j+1];
                        SumY =SumY+ TRGB[demision][column +j][row +i]*y[i+1][j+1];
                        }
                    }
                     ReturnTRGB[demision][column][row] = (int)Math.round(Math.atan((double)SumY/SumX));
              }
             }
        }

        return pixelsArrayToImage(TRGBArrayToPixelsArray(ReturnTRGB, imageInDimension), imageInDimension);
    }
   protected Image My_histogram(Image imageIn){
        Dimension imageInDimension = getImageDimension(imageIn);
        int TRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int ReturnTRGB[][][] = pixelArrayToTRGBArray(imageToPixelsArray(imageIn), imageInDimension);
        int[] histR = new int[256], histG = new int[256], histB = new int[256];
        int r = (int)imageInDimension.getWidth();
        int c = (int)imageInDimension.getHeight();
        for (int i = 0; i < r; i++)
                for (int j = 0; j < c ; j++){
                    histR[TRGB[1][i][j]]++;
                    histG[TRGB[2][i][j]]++;
                    histB[TRGB[3][i][j]]++;
        }

        for(int i=1; i<256; i++){

            histR[i] = histR[i-1] + histR[i];
            histG[i] = histG[i-1] + histG[i];
            histB[i] = histB[i-1] + histB[i];
        }

        for (int x = 0; x < imageInDimension.getWidth(); x++)
            for (int y = 0; y < imageInDimension.getHeight(); y++){
                ReturnTRGB[1][x][y]=Math.round(((histR[TRGB[1][x][y]])/(float)(r*c))*255);
                ReturnTRGB[2][x][y]=Math.round(((histG[TRGB[2][x][y]])/(float)(r*c))*255);
                ReturnTRGB[3][x][y]=Math.round(((histB[TRGB[3][x][y]])/(float)(r*c))*255);
            }
        return pixelsArrayToImage(TRGBArrayToPixelsArray(ReturnTRGB, imageInDimension), imageInDimension);
    }
    protected static int[][] getColorValues(int numSegments){
        Random rand = new Random();
        int[][] colorValues = new int[numSegments][4];
        for(int i = 0; i<numSegments; i++){
            int tr=0,rd=0,gr=0,bl=0;
            if(i==0){
                tr = 255; rd = 255; gr = 0; bl = 0;}
            else if(i==1){
                tr = 255; rd = 0; gr = 0; bl = 255;}
            else if(i==2){
                tr = 255; rd = 0; gr = 255; bl = 0;}
            else if(i==3){
                tr = 255; rd = 255; gr = 255; bl = 0;}
            else if(i==4){
                tr = 255; rd = 0; gr = 255; bl = 255;}
            else if(i==5){
                tr = 255; rd = 255; gr = 0; bl = 255;}
            else{
                tr = 255;
                rd = rand.nextInt(255);
                gr = rand.nextInt(255);
                bl = rand.nextInt(255);
            }

            colorValues[i][0] = tr;
            colorValues[i][1] = rd;
            colorValues[i][2] = gr;
            colorValues[i][3] = bl;
        }
        return colorValues;
    }

    protected static Dimension getImageDimension(Image imageIn){
        ImageIcon imageIconIn = new ImageIcon(imageIn);
        return new Dimension(imageIconIn.getIconWidth(), imageIconIn.getIconHeight());
    }

    protected Image pixelsArrayToImage(int pixels[], Dimension imageInDimension){
	return createImage(new MemoryImageSource((int) imageInDimension.getWidth(), (int) imageInDimension.getHeight(), pixels, 0, (int) imageInDimension.getWidth()));
    }

    protected static int[] imageToPixelsArray(Image imageIn){
	Dimension imageInDimension = getImageDimension(imageIn);
	int imagePixelLength = (int) (imageInDimension.getWidth() * imageInDimension.getHeight());
	int pixels[] = new int[imagePixelLength];
	PixelGrabber pixelGrabber = new PixelGrabber(imageIn, 0, 0, (int) imageInDimension.getWidth(), (int) imageInDimension.getHeight(), pixels, 0, (int) imageInDimension.getWidth());
        try{pixelGrabber.grabPixels();}
	catch(InterruptedException ie){	System.exit(1);}
	return pixels;
    }

    protected static int[] TRGBArrayToPixelsArray(int[][][] TRGB, Dimension imageInDimension){
        int imagePixelLength = (int) (imageInDimension.getWidth() * imageInDimension.getHeight());
        int pixels[] = new int[imagePixelLength];
        for (int column = 0, row = 0, pixelIndex = 0; pixelIndex < imagePixelLength; pixelIndex++){
            pixels[pixelIndex] = getTRGB(TRGB[0][column][row], TRGB[1][column][row], TRGB[2][column][row], TRGB[3][column][row]);
            if (++column == imageInDimension.getWidth()){
                column = 0;
                row++;
            }
        }
        return pixels;
    }

    protected static int[][][] pixelArrayToTRGBArray(int[] pixels, Dimension imageInDimension){
        int imagePixelLength = (int) (imageInDimension.getWidth() * imageInDimension.getHeight());
        int TRGB[][][] = new int[4][(int) imageInDimension.getWidth()][(int) imageInDimension.getHeight()];
        for (int column = 0, row = 0, pixelIndex = 0; pixelIndex < imagePixelLength; pixelIndex++){
            TRGB[0][column][row] = getTransparencyComponent(pixels[pixelIndex]);
            TRGB[1][column][row] = getRedComponent(pixels[pixelIndex]);
            TRGB[2][column][row] = getGreenComponent(pixels[pixelIndex]);
            TRGB[3][column][row] = getBlueComponent(pixels[pixelIndex]);
            if (++column == imageInDimension.getWidth()){
                column = 0;
                row++;
            }
        }
        return TRGB;
    }
    protected static int[][][] pixelArrayToTHSIArray(int[] pixels, Dimension imageInDimension){
        int imagePixelLength = (int) (imageInDimension.getWidth() * imageInDimension.getHeight());
        int THSI[][][] = new int[4][(int) imageInDimension.getWidth()][(int) imageInDimension.getHeight()];
        for (int column = 0, row = 0, pixelIndex = 0; pixelIndex < imagePixelLength; pixelIndex++){
            THSI[0][column][row] = getTransparencyComponent(pixels[pixelIndex]);
            THSI[1][column][row] = getHueComponent(pixels[pixelIndex]);
            THSI[2][column][row] = getSatComponent(pixels[pixelIndex]);
            THSI[3][column][row] = getIntComponent(pixels[pixelIndex]);
            if (++column == imageInDimension.getWidth()){
                column = 0;
                row++;
            }
        }
        return THSI;
    }

    protected final static int getTransparencyComponent(int pixel){
		return (pixel >> 24) & 0xff;
	}

    protected final static int getRedComponent(int pixel){
		return (pixel >> 16) & 0xff;
    }

    protected final static int getGreenComponent(int pixel){
	return (pixel >> 8) & 0xff;
    }

    protected final static int getBlueComponent(int pixel){
		return pixel & 0xff;
    }
    protected final static int getHueComponent(int pixel){
                return (pixel>>15) &0x1ff;
    }
    protected final static int getSatComponent(int pixel){
                return (pixel>>8) &0x8f;
    }
    protected final static int getIntComponent(int pixel){
                return pixel &0xff;
    }

    protected final static int getLumaComponent(int pixel){
	return (int)( 0.299 * getRedComponent(pixel)
                + 0.587 * getGreenComponent(pixel) + 0.114 * getBlueComponent(pixel));
    }

    protected final static int getBlueDifference(int pixel){
	return (int)(-0.16874 * getRedComponent(pixel)- 0.33126 * getGreenComponent(pixel) + 0.50000 * getBlueComponent(pixel));
    }

    protected final static int getRedDifference(int pixel){
	return (int)( 0.50000 * getRedComponent(pixel)- 0.41869 * getGreenComponent(pixel) - 0.08131 * getBlueComponent(pixel));
    }

    protected final static int getYComponent(int pixel){
	return (int)(0.299 * getRedComponent(pixel) + 0.587 * getGreenComponent(pixel) + 0.114 * getBlueComponent(pixel));
    }

    protected final static int getUComponent(int pixel){
	return (int)((getBlueComponent(pixel) - getYComponent(pixel)) * 0.492);
    }

    protected final static int getVComponent(int pixel){
	return (int)((getRedComponent(pixel) - getYComponent(pixel)) * 0.877);
    }

    protected final static float[] getHSBComponents(int pixel){
        float[] hsb = new float[3];
        Color.RGBtoHSB(getRedComponent(pixel), getGreenComponent(pixel), getBlueComponent(pixel), hsb);
        return hsb;
    }


    protected final static float[] getXYZComponents(int pixel){
	float[] xyz = new float[3];
       	float r, g, b, X, Y, Z;
        r = getRedComponent(pixel)/255.f; //R 0..1
	g = getGreenComponent(pixel)/255.f; //G 0..1
	b = getBlueComponent(pixel)/255.f; //B 0..1

        if (r <= 0.04045)
            r = r/12;
	else
            r = (float) Math.pow((r+0.055)/1.055,2.4);
	if (g <= 0.04045)
            g = g/12;
	else
            g = (float) Math.pow((g+0.055)/1.055,2.4);
	if (b <= 0.04045)
            b = b/12;
	else
            b = (float) Math.pow((b+0.055)/1.055,2.4);

	X =  0.436052025f*r + 0.385081593f*g + 0.143087414f *b;
	Y =  0.222491598f*r + 0.71688606f *g + 0.060621486f *b;
	Z =  0.013929122f*r + 0.097097002f*g + 0.71418547f  *b;

	xyz[1] = (int) (255*Y + .5);
	xyz[0] = (int) (255*X + .5);
	xyz[2] = (int) (255*Z + .5);

        return xyz;
    }

    public static float[] getLUVComponents(int pixel){
        float[] luv = new float[3];
        float r, g, b, X, Y, Z, yr, L;
	float eps = 216.f/24389.f;
	float k = 24389.f/27.f;
	float Xr = 0.964221f;
	float Yr = 1.0f;
	float Zr = 0.825211f;

        r = getRedComponent(pixel)/255.f;
        g = getGreenComponent(pixel)/255.f;
        b = getBlueComponent(pixel)/255.f;

        if (r <= 0.04045)
            r = r/12;
        else
            r = (float) Math.pow((r+0.055)/1.055,2.4);

        if (g <= 0.04045)
            g = g/12;
        else
            g = (float) Math.pow((g+0.055)/1.055,2.4);

        if (b <= 0.04045)
            b = b/12;
        else
            b = (float) Math.pow((b+0.055)/1.055,2.4);

        X =  0.436052025f*r + 0.385081593f*g + 0.143087414f *b;
        Y =  0.222491598f*r + 0.71688606f *g + 0.060621486f *b;
        Z =  0.013929122f*r + 0.097097002f*g + 0.71418547f  *b;

        float u, v, u_, v_, ur_, vr_;

        u_ = 4*X / (X + 15*Y + 3*Z);
        v_ = 9*Y / (X + 15*Y + 3*Z);

        ur_ = 4*Xr / (Xr + 15*Yr + 3*Zr);
        vr_ = 9*Yr / (Xr + 15*Yr + 3*Zr);

        yr = Y/Yr;

        if ( yr > eps )
            L =  (float) (116*Math.pow(yr, 1/3.) - 16);
        else
            L = k * yr;

        u = 13*L*(u_ -ur_);
        v = 13*L*(v_ -vr_);

        luv[0] = (int) (2.55*L + .5);
        luv[1] = (int) (u + .5);
        luv[2] = (int) (v + .5);

        return luv;
    }

    protected final static int getTRGB(int transparency, int red, int green, int blue){
	return (transparency << 24) | (red << 16) | (green << 8) | (blue);
    }

    protected final static int getTHSI(int transparency, int hue, int sat, int inte){
        return (transparency << 24) | (hue << 15) | (sat << 8) | (inte);
    }

    public static void main(String args[]){
	ImagePimp app = new ImagePimp();
    }
}

class MouseInputAdapter implements MouseInputListener
{
    public static JTextField red, green, blue;
    int compCount = 0;
    ImageFrame imgFr;
    int [] pxl;
    Dimension dimension;
    ImageFrame image;
    boolean compCountGrew = false;
    void eventOutput(String eventDescription, MouseEvent e){
        if(compCount<ImagePimp.desktopPane.getComponentCount())
            compCountGrew = true;

        if(ImagePimp.desktopPane.getComponentCount()>0 && compCountGrew){
            image = (ImageFrame) ImagePimp.desktopPane.getSelectedFrame();
            pxl = ImagePimp.imageToPixelsArray(image.getImage());
            dimension = ImagePimp.getImageDimension(image.getImage());
        }

        if(ImagePimp.desktopPane.getSelectedFrame().equals(e.getComponent())){
            if(pxl != null){
                if(((e.getY()-28)*(int)dimension.getWidth()+e.getX()-5)>=0 && (((e.getY()-28)*(int)dimension.getWidth()+e.getX()-5)<pxl.length)){
                    int r = ImagePimp.getRedComponent(pxl[(e.getY()-28)*(int)dimension.getWidth()+e.getX()-5]);
                    int g = ImagePimp.getGreenComponent(pxl[(e.getY()-28)*(int)dimension.getWidth()+e.getX()-5]);
                    int b = ImagePimp.getBlueComponent(pxl[(e.getY()-28)*(int)dimension.getWidth()+e.getX()-5]);
                    red.setText("R: "+r);
                    green.setText("G: "+g);
                    blue.setText("B: "+b);
                }
            }

            if(e.getID() == e.MOUSE_EXITED){
                red.setText("");
                green.setText("");
                blue.setText("");
            }
        }

        compCount = ImagePimp.desktopPane.getComponentCount();
    }

    public void mouseMoved(MouseEvent e){
        eventOutput("Mouse moved", e);
    }

    public void mouseDragged(MouseEvent e){
        eventOutput("Mouse dragged", e);
    }

    public void mouseExited(MouseEvent e){
        eventOutput("Mouse exited", e);
    }

    public void mouseEntered(MouseEvent e){
        eventOutput("Mouse entered", e);
    }

    public void mouseReleased(MouseEvent e){
        eventOutput("Mouse released", e);
    }

    public void mousePressed(MouseEvent e){
        eventOutput("Mouse pressed", e);
    }

    public void mouseClicked(MouseEvent e){
        eventOutput("Mouse clicked", e);
    }
}

class ImageFrame extends JInternalFrame{
    private File imageFile;
    private ImagePanel imagePanel;

    public ImageFrame(File imageFile){
        // Call super class, JInternalFrame constructor
        super(imageFile.getName(), false, true, false);

        // Attempt to load image
        imagePanel = new ImagePanel(imageFile);
        getContentPane().add(imagePanel);

        // Set the internal image file to the passed arguement
        this.imageFile = imageFile;
        this.setTitle(imageFile.getName());
        // Initialize and show the internal frame window
        setSize(imagePanel.getImageIcon().getIconWidth(), imagePanel.getImageIcon().getIconHeight());
        show();
        toFront();
    }

    public ImageFrame(Image image){
        // Call super class, JInternalFrame constructor
        super("Untitled", false, true, false);

        // Attempt to load image
        imagePanel = new ImagePanel(image);
        getContentPane().add(imagePanel);

        // Set the internal image file to a default name
        imageFile = null;

        // Initialize and show the internal frame window
        setSize(imagePanel.getImageIcon().getIconWidth(), imagePanel.getImageIcon().getIconHeight());
        show();
        toFront();
    }

    public Image getImage(){
        // Return the image icon from the image panel, after conversion to an image
        return imagePanel.getImageIcon().getImage();
    }

    private class ImagePanel extends JPanel{
        private ImageIcon imageIcon;

        public ImagePanel(File imageFile){
                // Load the image
                imageIcon = new ImageIcon(imageFile.toString());
        }

        public ImagePanel(Image image){
                // Load the image
                imageIcon = new ImageIcon(image);
        }

        public ImageIcon getImageIcon(){
                // Return the image icon
                return imageIcon;
        }

        @Override
        public void paintComponent(Graphics g){
                // Paint the icon to the panel
                imageIcon.paintIcon(this, g, 0, 0);
        }
    }
}