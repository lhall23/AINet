//AINet.java
// 	-Lee Hall Tue 16 Oct 2012 11:03:15 AM EDT
// 	Refactored from ImagePimp.java

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.io.FileReader;
import java.io.FileWriter;
import java.awt.Dimension;
import java.awt.Image;
import java.text.ParseException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;


public class AINet {

    // Default values
    private static final String DEFAULT_TRAINING_FILE="test_data/ground_training.txt";
    private static final String DEFAULT_DATA_FILE="test_data/ground.txt";
    private static final int DEFAULT_POPULATION_SIZE=500;
    private static final int DEFAULT_MAX_BREEDING_ITER = 500;
    private static final int DEFAULT_DIMENSIONS = 3;

    //These should not be statics, but a property of each instantiation, but
    //we'll deal with that later.
    
    //Dimensionality of the Antigen/Antibody vectors
    private static int Dimensions=DEFAULT_DIMENSIONS;
    //Maximum number of passes to attempt convergance 
    private static int MaxIter = DEFAULT_MAX_BREEDING_ITER;
    //Number of Antibodies in each population
    private static int BaseScale = DEFAULT_POPULATION_SIZE;

    // *FIXME* Not a global. This goes away with refactoring
    private static File input_file;

	public static void main (String[] args){
        String optarg_s="";
        File corpus_file=null;
        File output_file=null;
        String msg="";

        final String help = "Usage: \n\t-h (help)\n" +
            "\t-t FILENAME \tTraining set\n" +
            "\t-f FILENAME \tInput file\n" +
            "\t-o FILENAME \tOutput file for classified results\n" +
            "\t\tIf not specified, this goes to the console.\n" +
            "\t-s SCALE \tScale of the clonal population to generate\n" +
            "\t-i ITERATIONS \tNumber of Iterations to train Antibodies\n" +
            "\t-d DIMENSIONS \tNumber of dimensions in data set\n" +
            "\t-p          \tparseable output\n" +
            "\t-T          \ttesting output\n";

                // Parse arguments... where's my beloved optarg?!
        int i=0;
        char opt=' ';
        while ((i < args.length) && (args[i].charAt(0) == '-')){
            opt = args[i++].charAt(1);
            optarg_s="";
            //Parse arguments that don't take options 
            switch(opt){
                case 'h':
                    System.out.print(help);
                    System.exit(1);
                case 'p':
                    //Currently a null op
                    continue;
                case 'T':
                    //Currently a null op
                    continue;
            }

            //If we made it here, we need an argument
            if  ((i < args.length) && (args[i].charAt(0) != '-')){
                optarg_s=args[i++];
            } else {
                System.out.println(
                    String.format("Argument required for option %c.", opt));
                System.out.print(help);
                System.exit(1);
            }

            switch(opt){
                case 't':
                    msg=String.format("Loading training file \"%s\".", optarg_s);
                    System.out.println(msg);
                    corpus_file=new File(optarg_s); 
                    if (!corpus_file.canRead()){
                        System.out.printf(
                            "Can't read training file %s. Exiting.", 
                            corpus_file.getName());
                        System.exit(1);
                    }
                    break;
                case 'f':
                    msg=String.format(
                        "Loading data file \"%s\".", optarg_s);
                    System.out.println(msg);
                    input_file=new File(optarg_s); 
                    if (!input_file.canRead()){
                        System.out.printf(
                            "Can't read input file %s. Exiting.", 
                            input_file.getName());
                        System.exit(1);
                    }
                    break;
                case 'o':
                    msg=String.format(
                        "Creating output file \"%s\".", optarg_s);
                    System.out.println(msg);
                    output_file=new File(optarg_s); 
                    break;
                case 's':
                    msg=String.format(
                        "Setting scale to \"%s\".", optarg_s);
                    System.out.println(msg);
                    BaseScale=Integer.valueOf(optarg_s); 
                    break;
                case 'i':
                    msg=String.format(
                        "Setting maximum iterations to \"%s\".", optarg_s);
                    System.out.println(msg);
                    MaxIter=Integer.valueOf(optarg_s); 
                    break;
                case 'd':
                    msg=String.format(
                        "Setting dimensions to \"%s\".", optarg_s);
                    System.out.println(msg);
                    Dimensions=Integer.valueOf(optarg_s); 
                    break;
                default:
                    System.out.print(help);
                    System.exit(1);
                    break;
            }
        }
        if (i != args.length){
            System.out.println("Unused arguments.");
            System.out.print(help);
            System.exit(1);
        }


        if (corpus_file == null) {
            msg=String.format("Loading default training file \"%s\".", 
                DEFAULT_TRAINING_FILE);
            System.out.println(msg);
            corpus_file=new File(DEFAULT_TRAINING_FILE);
        } 
        if (input_file == null) {
            msg=String.format("Loading default data file \"%s\".", 
                DEFAULT_DATA_FILE);
            System.out.println(msg);
            input_file=new File(DEFAULT_DATA_FILE);
        } 


        /* 
         * Moved from setupAINet(), which should be passed the data in an array
         * format. Otherwise we have different codepaths for loading test data
         * files and loading images from the interface.
         */

        // Read the input data file
        BufferedReader reader;
        ArrayList<Antigen> tempData = new ArrayList<Antigen>();
        String line_in=null;
        try {
            reader=new BufferedReader(new FileReader(input_file));
            while((line_in=reader.readLine()) != null){
                if (line_in.equals("")){
                    System.out.println("Blank line in input file skipped");
                    continue;
                }
                //Create an unclassified Antigen from the input row
                tempData.add(Antigen.valueOf(line_in,false));
            }
        }
        catch (FileNotFoundException e){
            msg=String.format("Failed to open data file %s", 
                input_file.getName());
            System.out.println(msg);
            System.exit(1);
        }
        catch (IOException e){
            msg=String.format("Error %s reading file %s", 
                e, input_file.getName());
            System.out.println(msg);
            System.exit(1);
        }
        catch (ParseException e){
            msg=String.format("Error %s reading file %s", 
                e, input_file.getName());
            System.out.println(msg);
            System.exit(1);
        }

        Antigen[] inputData=tempData.toArray(new Antigen[tempData.size()]);
        
	    AINet ais = new AINet(inputData,corpus_file);  	
        System.out.println("Classification results: ");
        int m;
        if (output_file == null){  
            for (m=0; m < ais.Whole_Ag.length; m++){
                System.out.println(ais.Whole_Ag[m]);
            }
        } else {
            try {
                BufferedWriter writer=
                    new BufferedWriter(new FileWriter(output_file));
                for (m=0; m < ais.Whole_Ag.length; m++){
                    writer.write(ais.Whole_Ag[m].toString());
                    writer.newLine();
                }
                writer.close();
            }
            catch (IOException e){
                msg=String.format("Error %s writing classification to %s.",
                    e, output_file.getName());
                System.out.println(msg);
                System.exit(1);
            }

        }
	}

    //bird training = 213
    //scene training = 3400
    //ground training = 6425
    //bird testing = 20584
    //scene testing = 18369
    //ground testing = 262144
    // private static final int Training_AgScale=6425;
    // private static final int AgScale=262144;
    private int Training_AgScale;
    private int AgScale;

    private static int MaxValue=255;
    private static final int diversityCount=BaseScale*3;
    private static final int Clonal_BaseScale=BaseScale*3;
    private static final int Initial_AbScale=1000;
    private static double supression_threshold = 0.09;
    private static double metadynamics_threshold = 0.7;

    /*
     * New Class vars
     */

    private Antigen Whole_Ag[];
    Dimension imageInDimension;
    int TRGB[][][];
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

    public static abstract class Cell implements Cloneable{
        protected static final int DEFAULT_CLASS=0;
        private static final int DEFAULT_VAL=0;
        //Regex to split input strings
        protected static String split_on= "[, \t]+";

        public double value[]=new double[Dimensions];
        public int classification;

        //Characters on which to create the 

        public Cell(double[] values, int classification){
            for(int i=0;i<Dimensions;i++){
                this.value[i]=values[i];
            }
            this.classification=classification;
        }

        public Cell(double[] values){
            this(values,DEFAULT_CLASS);
        }

        public Cell(int[] values){
            this(promote(values),DEFAULT_CLASS);
        }

        public Cell(){
            for(int i=0;i<Dimensions;i++){
                this.value[i]=DEFAULT_VAL;
            }
            this.classification=DEFAULT_CLASS;
        }

        public void setValue(int i, double f) {
            this.value[i]=f;
        }

        public String toString(){
            StringBuilder retString = new StringBuilder();
            for (int i=0; i < Dimensions; i++){
                retString.append(String.format("%3f ", this.value[i]));
            }
            retString.append(String.format("%d",this.classification));
            return retString.toString();
        }
        //Promote an array of ints to an array of doubles
        private static double[] promote(int[] input){
            double[] output = new double[input.length];
            for (int i=0; i < input.length; i++){
                output[i]=input[i];
            }
            return output;
        }

    }
    
    // This is represents the vectors we will be classifying
    public static class Antigen extends Cell{

        public double AgValue[];
        public Antigen(){
            super();
        }
        public Antigen(double[] values, int classification){
            super(values,classification);
        }
        public Antigen(int[] values){
            super(values);
        }

        public void setValue(int i, double f) {
            this.value[i]=f;
        }

        public static Antigen valueOf(String input) throws ParseException{
            return valueOf(input,true);
        }

        public static Antigen valueOf(String input, boolean classified) 
                throws ParseException{
            String[] str_values;
            double[] num_values=new double[Dimensions];
            int classification;
            String msg;

            int expected_dim=Dimensions+1;
            if (!classified){
                expected_dim=Dimensions;
            }

            str_values=input.split(Cell.split_on); 
            //Check to see if we got a string with the right number of
            //dimensions plus a classification

            if (str_values.length != expected_dim){
                msg="Wrong number of values for ";
                if (!classified){
                    msg+="un";
                } 
                msg+="classified data string. " +
                    String.format("Expected %d, got %d in \"%s\".", 
                        expected_dim, str_values.length,input);
                throw new ParseException(msg,-1);
            }

            for(int i=0; i < Dimensions; i++){
                num_values[i]=Double.valueOf(str_values[i]);
            }
            if (classified) {
                classification=Integer.valueOf(str_values[Dimensions]);   
            } else {
                classification=DEFAULT_CLASS;
            }
            return new Antigen(num_values,classification);
        }
    }

    //These represent the vectors used to classify data
    public static class Antibody extends Cell{

        private double Affinity=0;
        private Antigen Ag = new Antigen();

        public Antibody(){
            super();
        }

        public Antibody(double[] values){
            super(values);
        }

        public void setAntigen(Antigen Ag) {
            this.Ag=Ag;
        }

        public String toString(){
            return String.format("%s %f",super.toString(), Affinity);
        }

        public static Antibody valueOf(String input) throws ParseException{
            String[] str_values;
            double[] num_values=new double[Dimensions];

            str_values=input.split(Cell.split_on); 
            //Check to see if we got a string with the right number of
            //dimensions 
            if (str_values.length != Dimensions){
                String msg= 
                    "Wrong number of dimensions for current AINet instance." +
                    String.format("Expected %d, got %d in \"%s\"", Dimensions, 
                        str_values.length,input);
                throw new ParseException(msg,-1);
            }

            for(int i=0; i < Dimensions; i++){
                num_values[i]=Double.valueOf(str_values[i]);
            }
            return new Antibody(num_values);
        }
    }


    //To calculate the affinity between given Antibody and Antigen
    public static double getAffinity(Antigen Ag, Antibody Ab)
    {
        double EuclidianDistance = 0;
        for(int i=0;i<Dimensions;i++)
            if(Ag!=null&&Ab!=null)
            //EuclidianDistance=(double)EuclidianDistance+((Ag.value[i]-Ab.value[i])*(Ag.value[i]-Ab.value[i]));

        //return (double)(1/1+Math.sqrt(EuclidianDistance));
                EuclidianDistance = EuclidianDistance+Math.abs(Ag.value[i] - Ab.value[i]);
        return (1/EuclidianDistance);
    }

    //To calculate the affinity between two antibodies
    public static double getAffinity(Antibody Ab1, Antibody Ab2)
    {
        double EuclidianDistance = 0;
        for(int i=0;i<Dimensions;i++)
            if(Ab1!=null&&Ab2!=null)
           //  EuclidianDistance=(double)EuclidianDistance+((Ab1.value[i]-Ab2.value[i])*(Ab1.value[i]-Ab2.value[i]));
       // return (double)(1/1+Math.sqrt(EuclidianDistance));
                EuclidianDistance = EuclidianDistance+Math.abs(Ab1.value[i] - Ab2.value[i]);
        return (1/EuclidianDistance);
    }


    
//Manual assignment of one Antibody to another Antibody
// *FIXME* This should be a clone() method of the Antibody class
     public static void equate(Antibody Ab1,Antibody Ab2)
    {
         if(Ab1!=null&&Ab2!=null)
         {
        for(int i=0;i<Dimensions;i++)
          Ab1.value[i]=Ab2.value[i];

        Ab1.classification=Ab2.classification;
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
            //*FIXME* You don't need a deep copy here
            for(int j=0;j<BaseScale;j++)
                if(j==0||(getAffinity(Ag[i],ab)<getAffinity(Ag[i],Ab[j])))
                    equate(ab,Ab[j]);
            if(Ag[i]!=null&&ab!=null)
            if(Ag[i].classification==ab.classification)
                correct++;
        }
        return (double)correct/(double)AgScale;
    }

//Generate clone_population
    public void Clonal_Expansion(Antibody[] AbBase,ArrayList<Antibody> clonal_population,Antigen[] Training_Ag){

        int clone_count = 0, i = 0,j = 0;
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
              /*  for(int k = 0;k<Dimensions;k++){
                if(AbBase[i].Affinity/2>rand.nextDouble())
                ab.value[k]=(double) (AbBase[i].value[k]);
                else
                ab.value[k]=rand.nextDouble()*MaxValue;
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
                            clonal_population.get(j).classification=Training_Ag[i].classification;
                            clonal_population.get(j).Ag=Training_Ag[i];
                            }
                        }
                     }
                  }
     /*   System.out.print("\nclonal expansion :\n");
        for(i = 0;i < clonal_population.size(); i++)
            System.out.println(clonal_population.get(i));
      *
      */
    }

    //Mutate the clone population inversly proportional to affinity
    public void Affinity_Maturation(ArrayList<Antibody> clonal_population,
            Antibody[] AbBase,Antigen[] Training_Ag, double correctness){

        int total_clone_count = clonal_population.size();
        for(int i = 0;i < total_clone_count;i++){
            float alpha = (float)(1/clonal_population.get(i).Affinity);
            for(int j = 0;j<Dimensions;j++){
                clonal_population.get(i).value[j] = 
                    clonal_population.get(i).value[j] + alpha;
                    //* (clonal_population.get(i).Ag.value[j] - 
                    //  clonal_population.get(i).value[j]);
            }
        }

        //After affinity maturation recalculate the affinity of clonal population and the class it belongs to
        for(int j=0;j<clonal_population.size();j++){
            for(int i=0;i<Training_AgScale;i++){
                if(clonal_population.get(j)!=null&&Training_Ag[i]!=null){
                    if(i==0|| (
                            clonal_population.get(j).Affinity < 
                                getAffinity(Training_Ag[i],
                                    clonal_population.get(j)))) {
                        if(clonal_population.get(j)!=null&&
                                Training_Ag[i]!=null){
                            clonal_population.get(j).Affinity = 
                                getAffinity(Training_Ag[i],
                                    clonal_population.get(j));
                            clonal_population.get(j).classification=
                                Training_Ag[i].classification;
                            clonal_population.get(j).Ag=Training_Ag[i];
                        }
                    }
                }
            }
        }

       /* System.out.println("\nafter affinity maturation :\n" );
        for(int i = 0;i < clonal_population.size(); i++)
            System.out.println(clonal_population.get(i));
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
            System.out.println(clonal_population.get(i));
        */
    }


    //Remove those clones whose affinity with each other is less than the supression threshold
    public static void Clonal_Supression(ArrayList<Antibody> clonal_population,ArrayList<Antibody> final_clonal_population){
        int i = 0,j = 0;
        int size = clonal_population.size();
        for(i=0;i<size;i++){
            for(j=i+1;j<size;j++){
                if(clonal_population.get(i)!= null && clonal_population.get(j)!= null)
                if(getAffinity(clonal_population.get(i), clonal_population.get(j)) < supression_threshold)
                {
                    break;
                }
            }//inner for loop
            if(j == size)
                final_clonal_population.add(clonal_population.get(i));
        }//outer for loop

    }


//concatenate the antibody base with the final clonal population
    public static void Network_Reconstruction(ArrayList<Antibody> Reconstructed_Antibody_Pool,ArrayList<Antibody> final_clonal_population,Antibody[] AbBase){

       for(int i = 0;i<BaseScale;i++)
           Reconstructed_Antibody_Pool.add(AbBase[i]);

       for(int j = 0;j<final_clonal_population.size();j++){
           Reconstructed_Antibody_Pool.add(final_clonal_population.get(j));

        }
    }

 //network supression
    public static void Network_Interaction_Supression(ArrayList<Antibody> Reconstructed_Antibody_Pool,ArrayList<Antibody> final_Reconstructed_Antibody_Pool){
      int i = 0,j = 0;
            int size = Reconstructed_Antibody_Pool.size();
            for(i=0;i<size;i++){
                for(j=i+1;j<size;j++){
                    if(Reconstructed_Antibody_Pool.get(i)!= null && Reconstructed_Antibody_Pool.get(j)!= null)
                    if(getAffinity(Reconstructed_Antibody_Pool.get(i), Reconstructed_Antibody_Pool.get(j)) < supression_threshold)
                    {
                        break;
                    }
                }//inner for loop
                if(j == size)
                    final_Reconstructed_Antibody_Pool.add(Reconstructed_Antibody_Pool.get(i));
            }//outer for loop

    }


    //Introduce diversity to continue the while loop
    public void Introduce_Diversity(ArrayList<Antibody> 
            final_Ab_Pool,Antigen[] Training_Ag){

        Random rand  = new Random();
        Antibody Ab ;
        for(int i = 0;i<diversityCount;i++) {
            Ab = new Antibody();
            for (int j = 0; j < Dimensions; j++) {
               Ab.setValue(j,MaxValue * rand.nextDouble());
            }
            final_Ab_Pool.add(Ab);
        }

        for(int j=0;j<final_Ab_Pool.size();j++) {
            for(int i=0;i<Training_AgScale;i++) {
                if(final_Ab_Pool.get(j)!=null&&Training_Ag[i]!=null){
                    if(i==0||( final_Ab_Pool.get(j).Affinity<
                            getAffinity(Training_Ag[i],final_Ab_Pool.get(j)))){
                        final_Ab_Pool.get(j).Affinity = 
                            getAffinity(Training_Ag[i],
                                final_Ab_Pool.get(j));
                        final_Ab_Pool.get(j).classification=
                            Training_Ag[i].classification;
                        final_Ab_Pool.get(j).Ag=Training_Ag[i];
                    }
                }
            }
        }
    }

    //Load the training set from a file and return it
    public static Antigen[] get_training_set(File corpus_file){
        Antigen Training_Ag[];
        ArrayList<Antigen> temp_training_ag = new ArrayList<Antigen>();
        String line_in=null;
        int Training_AgScale;
        String msg;
        
        // Read the training data file

        try{
            BufferedReader reader = 
                new BufferedReader(new FileReader(corpus_file));
            while((line_in=reader.readLine()) != null){
                if (line_in.equals("")){
                    System.out.println("Blank line in input file skipped");
                    continue;
                }
                temp_training_ag.add(Antigen.valueOf(line_in));
            }
        }
        catch (FileNotFoundException e){
            msg=String.format("Failed to open data file %s", 
                input_file.getName());
            System.out.println(msg);
            System.exit(1);
        }
        catch (IOException e){
            msg=String.format("Error %s reading file %s", 
                e, input_file.getName());
            System.out.println(msg);
            System.exit(1);
        }
        catch (ParseException e){
            msg=String.format("Error %s reading file %s", 
                e, input_file.getName());
            System.out.println(msg);
            System.exit(1);
        }

        //*FIXME* These should all be using Lists 
        Training_AgScale=temp_training_ag.size();
        Training_Ag=temp_training_ag.toArray(
            new Antigen[Training_AgScale]);

        System.out.println("Dumping training set:");
        for(int i=0; i < Training_AgScale; i++){
            System.out.printf("[%d]: %s\n", i, Training_Ag[i]);
        }
        return Training_Ag;
    }



    public void setupAINet(Antigen Whole_Ag[], Antigen Training_Ag[]){


        Antibody Initial_Ab[]=new Antibody[Initial_AbScale];
        Antibody AbBase[]=new Antibody[BaseScale];
        //private  Antigen Whole_Ag[]=new Antigen[AgScale];
        ArrayList<Antibody> Reconstructed_Antibody_Pool = 
            new ArrayList<Antibody>(BaseScale+Clonal_BaseScale+diversityCount);
        ArrayList<Antibody> final_Reconstructed_Antibody_Pool = 
            new ArrayList<Antibody>(BaseScale+Clonal_BaseScale+diversityCount);
        ArrayList<Antibody> clonal_population = new ArrayList<Antibody>(Clonal_BaseScale);
        ArrayList<Antibody> final_clonal_population = new ArrayList<Antibody>(Clonal_BaseScale);
        //   initialize(Initial_Ab,AbBase ,Whole_Ag,Training_Ag);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////

      //randomly generate all the antibodies
        Random random=new Random();

        for(int i=0;i<Initial_AbScale;i++)
        { Initial_Ab[i]=new Antibody();
            for(int j=0;j<Dimensions;j++)
            {
             // random=new Random();
                if(Initial_Ab[i]!=null)
               Initial_Ab[i].setValue(j,random.nextDouble()*MaxValue);
            }
        }

// initialise the whole antigen community 
// This is now passed in as an argument and doesn't need to be initialiazed
// here

//Initialise the training antigen



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
                    Initial_Ab[j].classification=Training_Ag[i].classification;
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
                  // *FIXME* 
                  // Is this supposed to be bubble sort?
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

        // double[] y=new double[1000]; //This is apparently used to calculate
        // the affinity for the whole dataset, but all the code using it is
        // commented out.
        int m=0;
        while(true){
            correctness_current_iteration = 
                Whole_Affinity(AbBase,Training_Ag,Training_AgScale);
            System.out.println("after iteration "+ iter_count+" whole affinity is "+correctness_current_iteration);
            if(correctness_current_iteration > 0.99 || m >=20 || 
                    iter_count > MaxIter){
                System.out.printf("Breaking while loop after %d times.\n", 
                    iter_count);
                break;
             } else {
                iter_count++;
                Clonal_Expansion(AbBase,clonal_population,Training_Ag);
                Affinity_Maturation(clonal_population,AbBase,Training_Ag,
                    correctness_current_iteration);
                Metadynamics(clonal_population);
                Clonal_Supression(clonal_population,final_clonal_population);
                Network_Reconstruction(Reconstructed_Antibody_Pool, 
                    final_clonal_population, AbBase);
                Network_Interaction_Supression(Reconstructed_Antibody_Pool,
                    final_Reconstructed_Antibody_Pool);
                Introduce_Diversity(final_Reconstructed_Antibody_Pool,
                    Training_Ag);

                //Find the top 'baseScale' antibodies from the final
                //reconstructed antibody pool and repeate the loop

                int c=0;
                for(int i=0;i<BaseScale;i++){
                    AbBase[i]=new Antibody();
                    equate(AbBase[i],final_Reconstructed_Antibody_Pool.get(i));
                }
               
                // *FIXME*  
                //What does the magic number 10 do here? 
                //(Other than cause a crash when it's larger than BaseScale)
                for(int j=0;j<10;j++) {
                    for(int i=10;i<final_Reconstructed_Antibody_Pool.size();
                            i++) {
                        if((AbBase[j].Affinity<
                            final_Reconstructed_Antibody_Pool.get(i).Affinity))
                        {
                            equate(AbBase[j],
                                final_Reconstructed_Antibody_Pool.get(i));
                            c=i;
                        }
                        final_Reconstructed_Antibody_Pool.get(c).Affinity = 0;
                    } 
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
            System.out.println(AbBase[i]);
            
           //  System.out.println("Whole Affinity = "+Whole_Affinity(AbBase,Whole_Ag,AgScale));
           //  System.out.println("Total number of iterations = "+iter_count);
     //}//end of runainet

             Antibody ab = new Antibody();
         for(int i=0;i<AgScale;i++)
        {
            equate(ab,AbBase[1]);
            for(int j=1;j<BaseScale;j++)
            {
                if(getAffinity(Whole_Ag[i],AbBase[j])>getAffinity(Whole_Ag[i],ab))
                {
                    Whole_Ag[i].classification=AbBase[j].classification;
                    equate(ab,AbBase[j]);
                }
            }
        }
         
    }

    // *KILLME*
    public AINet(Image imageIn) throws Exception {
    //Pass this value downstream. We'd like to deprecate this constructor
    this(ImagePimp.pixelsArrayToTRGBArray(
            ImagePimp.imageToPixelsArray(imageIn), 
            ImagePimp.getImageDimension(imageIn)
        ));
    // this.imageInDimension=ImagePimp.getImageDimension(imageIn);
    // this.TRGB = ImagePimp.pixelsArrayToTRGBArray(ImagePimp.imageToPixelsArray(imageIn), imageInDimension);
    // This is for reading input from a text file.
    //Antigen [] Whole_Ag=new Antigen[AgScale];

    }

   
    // Take in a 3-d TRGB array and dump it into a 2-d array, 
    // passing it on the the eventual constructor 
    // This is currently stupidly inefficient, but these two legacy
    // constructors will go away as soon as we can actually test things
    // *KILLME*
    public AINet(int[][][] TRGB) throws Exception {
        this(TRGBto2dArray(TRGB), new File(DEFAULT_TRAINING_FILE));     
    }

    public static int[][] TRGBto2dArray(int[][][] TRGB){
        int row_len=TRGB[0][0].length;
        int col_len=TRGB[0].length;
        int[][] inputData = new int[row_len*col_len][Dimensions]; 
        System.out.printf("Rows: %d, Cols: %s\n", row_len, col_len);
        //There are 4 bands, but we don't care about the transparency
        int bands=3;

        //Following loop from getResults() -- rowmajor or column major flattening
        //shouldn't matter as long as it's consistent
        for (int row=0; row < row_len; row++){
            for (int col=0; col < col_len; col++){
                for (int k=0; k < bands; k++){
                    inputData[row * col_len + col][k]=TRGB[k+1][col][row];
                }
            }
        }
        return inputData;
    }

    /* 
     * This will be the final constructor. If a 2d array is passed in and
     * returned, the AINet module doesn't need to know anything about images,
     * ImagePimps array conversions or the dimensions of what is being looked
     * at.
     */
    public AINet(int[][] inputData, File corpus_file) throws Exception{
        //*KILLME* This makes sure that TRGB is initialized for getResults()
        //when this is the only constructor called. Since getResults should be
        //returning a flattened array anyway, the original dimensions are
        //irrelevant
        this.TRGB=new int[4][inputData.length][1];

        Whole_Ag=new Antigen[inputData.length];
        for (int i=0; i < inputData.length; i++){
            Whole_Ag[i]=new Antigen(inputData[i]);
        }

        Antigen[] Training_Ag=get_training_set(corpus_file);
        //*FIXME* We should be checking this when we use it, not relying on a
        //global
        AgScale=Whole_Ag.length;
        Training_AgScale=Training_Ag.length;
        System.out.println("Antibodies:");
        for(int n=0; n < Training_Ag.length; n++){
            System.out.println(Training_Ag[n]);
        }
        setupAINet(Whole_Ag,Training_Ag);
    }

    public AINet(Antigen[] inputData, File corpus_file){
        //*KILLME* This makes sure that TRGB is initialized for getResults()
        //when this is the only constructor called. Since getResults should be
        //returning a flattened array anyway, the original dimensions are
        //irrelevant
        this.TRGB=new int[4][inputData.length][1];

        Whole_Ag=inputData;

        Antigen[] Training_Ag=get_training_set(corpus_file);
        //*FIXME* We should be checking this when we use it, not relying on a
        //global
        AgScale=Whole_Ag.length;
        Training_AgScale=Training_Ag.length;
        System.out.println("Antibodies:");
        for(int n=0; n < Training_Ag.length; n++){
            System.out.println(Training_Ag[n]);
        }
        setupAINet(Whole_Ag,Training_Ag);
    }

    public int[] getResults(){
    int i=0;
        System.out.printf("Generating TRGB array of size %d,%d,%d\n",
            TRGB.length, TRGB[0].length,TRGB[0][0].length);
        for(int row=0;row<TRGB[0][0].length;row++)
        for (int column = 0; column <TRGB[0].length; column++)
        {
            if(Whole_Ag[i] != null) {
                TRGB[0][column][row]=255;
                if(Whole_Ag[i].classification==1)
                {
                    TRGB[1][column][row]=255;//55;
                    TRGB[2][column][row]=222;//255;//128;
                    TRGB[3][column][row]=10;//78;
                }
                else if(Whole_Ag[i].classification==2)
                {
                    TRGB[1][column][row]=0;
                    TRGB[2][column][row]=0;//64;//75;
                    TRGB[3][column][row]=215;//78;
                }
                else if(Whole_Ag[i].classification==3)
                {
                    TRGB[1][column][row]=0;
                    TRGB[2][column][row]=215;
                    TRGB[3][column][row]=0;
                }
                else if(Whole_Ag[i].classification==4)
                {
                    TRGB[1][column][row]=215;
                    TRGB[2][column][row]=0;
                    TRGB[3][column][row]=0;
                }
                else if(Whole_Ag[i].classification==5)
                {
                    TRGB[1][column][row]=0;
                    TRGB[2][column][row]=10;
                    TRGB[3][column][row]=0;
                }
            } 
            i++;

        }

    /*for(int row=0;row<imageInDimension.getHeight();row++)
        for (int column = 0; column < imageInDimension.getWidth(); column++)
        {
            System.out.println(TRGB[1][column][row]+"  "+TRGB[2][column][row]+"  "+TRGB[3][column][row]+"  3");

        }
    System.out.println(imageInDimension.getHeight()*imageInDimension.getWidth());*/
    //return ImagePimp.pixelsArrayToImage(ImagePimp.TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);
    return ImagePimp.TRGBArrayToPixelsArray(TRGB, 
        new Dimension(TRGB[0][0].length, TRGB[0].length));
}

}


