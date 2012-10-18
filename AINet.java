//AINet.java
// 	-Lee Hall Tue 16 Oct 2012 11:03:15 AM EDT
// 	Refactored from ImagePimp.java

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.FileReader;
import java.awt.Dimension;
import java.awt.Image;


public class AINet {

    private static final String TRAINING_FILE="test_data/ground_training.txt";
    private static final String DATA_FILE="test_data/ground.txt";
    private static final int DIMENSIONS=3;

    // *FIXME* Not a global. This goes away with refactoring
    private static File input_file;

	public static void main (String[] args) throws Exception{
        String optarg_s="";
        File corpus_file=null;

        final String help = "Usage: \n\t-h (help)\n" +
            "\t-t FILENAME \tTraining set\n" +
            "\t-f FILENAME \tInput file\n" +
            "\t-p          \tparseable output\n" +
            "\t-T          \ttesting output\n";

                // Parse arguments... where's my beloved optarg?!
        int i= 0;
        char opt=' ';
        parse_options:
        while ((i < args.length) && (args[i].charAt(0) == '-')){
            //If there's an argument after the option, grab it.
            //Make sure we're not testing against the last round
            optarg_s="";
            if (args[i].length() > 2){
                optarg_s=args[i].substring(2);
            }
            opt = args[i++].charAt(1);

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

            //If we don't already have an argument and we make it here, 
            //find one
            if (optarg_s == "") {
                if  ((i < args.length) && (args[i].charAt(0) != '-')){
                    optarg_s=args[i++];
                }             }
            }

            switch(opt){
                case 't':
                    corpus_file=new File(optarg_s); 
                    if (!corpus_file.canRead()){
                        System.out.printf(
                            "Can't read training file %s. Exiting.", 
                            corpus_file.getName());
                        System.exit(1);
                    }
                    break;
                case 'f':
                    input_file=new File(optarg_s); 
                    if (!input_file.canRead()){
                        System.out.printf(
                            "Can't read input file %s. Exiting.", 
                            input_file.getName());
                        System.exit(1);
                    }
                    break;
                default:
                    System.out.print(help);
                    System.exit(1);
                    break;
            }

            if (corpus_file == null) {
                corpus_file=new File(TRAINING_FILE);
            } 
            if (input_file == null) {
                input_file=new File(DATA_FILE);
            } 


        /* 
         * Moved from setupAINet(), which should be passed the data in an array
         * format. Otherwise we have different codepaths for loading test data
         * files and loading images from the interface.
         *
         * *FIXME* This should eventually take in doubles, but we need to deal
         * with the final output first, so we'll stick with ints
         */

        FileReader fin;
        Scanner src;
        int[] row = new int[DIMENSIONS];
        ArrayList<int[]> tempData = new ArrayList<int[]>();
        try{
            fin=new FileReader(input_file);
            src=new Scanner(fin);

            int records=0, row_count=0;
            while(src.hasNext()){
                row[records%DIMENSIONS]=src.nextInt();
                if (records++%DIMENSIONS==DIMENSIONS-1){ 
                    tempData.add(row);
                }
            }
            if (records%DIMENSIONS != 0) {
                System.out.print("Unexpected data size in " + 
                    DATA_FILE + "\n");
            }
        }catch(IOException e){
            System.out.print(e);
        }
        int[][] inputData=tempData.toArray(
            new int[DIMENSIONS][tempData.size()]);
	    AINet ais=new AINet(inputData);  	
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

    /*
     * New Class vars
     */

    private  Antigen Whole_Ag[];
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



    public void setupAINet(Antigen Whole_Ag[]) throws IOException {

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

//Initialise the training antigen
        FileReader fin;
        Scanner src;

        try{
        fin=new FileReader(TRAINING_FILE);
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
                    Whole_Ag[i].AgClass=AbBase[j].AbClass;
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
        this(TRGBto2dArray(TRGB));     
    }

    public static int[][] TRGBto2dArray(int[][][] TRGB){
        int row_len=TRGB[0][0].length;
        int col_len=TRGB[0].length;
        int[][] inputData = new int[row_len*col_len][NDimention]; 
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
    public AINet(int[][] inputData) throws Exception{
        //*KILLME* This makes sure that TRGB is initialized for getResults()
        //when this is the only constructor called. Since getResults should be
        //returning a flattened array anyway, the original dimensions are
        //irrelevant
        this.TRGB=new int[4][inputData.length][1];

        //*FIXME* Make a better constructor for Antigens
        Whole_Ag=new Antigen[inputData.length];
        for (int i=0; i < inputData.length; i++){
            Whole_Ag[i]=new Antigen();
            for (int j=0; j < inputData[0].length; j++){
                Whole_Ag[i].AgValue[j]=inputData[i][j];
            }
        }
        setupAINet(Whole_Ag);
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


