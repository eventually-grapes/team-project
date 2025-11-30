import java.util.HashMap;
import java.awt.Color;

public class TierList{
    public HashMap<Integer, Tier> tiers;

    public TierList(){
         // adds default tiers S, A, B, C, D, F with white color
        this.tiers = new HashMap<Integer, Tier>();
        this.tiers.put(0, new Tier(new ItemList(), Color.WHITE, "S"));
        this.tiers.put(1, new Tier(new ItemList(), Color.WHITE, "A"));
        this.tiers.put(2, new Tier(new ItemList(), Color.WHITE, "B"));
        this.tiers.put(3, new Tier(new ItemList(), Color.WHITE, "C"));
        this.tiers.put(4, new Tier(new ItemList(), Color.WHITE, "D"));
        this.tiers.put(5, new Tier(new ItemList(), Color.WHITE, "F"));
    }
    public static void addTier(Tier tier){}
    public static void removeTier(int position){}
    public static void moveTier(int position, boolean up){}
}