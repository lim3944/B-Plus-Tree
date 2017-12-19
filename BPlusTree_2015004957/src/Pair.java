import java.io.Serializable;

public class Pair <F,S> implements Serializable{

	private static final long serialVersionUID = 1L;
	public F first;
    public S second;

    public Pair() {
    	
    }
    
    public Pair(F first, S second){
      this.first = first;
      this.second = second;
    }

    static <F,S> Pair<F,S> of(F first, S second){
        return new Pair<F,S>(first, second);
    }
}