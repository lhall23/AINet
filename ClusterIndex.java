//ClusterIndex.java
// 	-Lee Hall Sun 23 Dec 2012 07:03:03 PM EST

public class ClusterIndex{
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
