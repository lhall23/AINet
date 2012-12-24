//ColorsClusterIndex.java
// 	-Lee Hall Sun 23 Dec 2012 06:53:49 PM EST
//
// 	Stripped out of ImagePimp. 
//
// 	*FIXME* How about we use some reasonable variable names
// 	and data structures here?

public class ColorsClusterIndex {
    private int color1, color2, color3, cluster, index;
    public ColorsClusterIndex(int one, int two,
            int three, int c, int i){
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

