import java.util.HashMap;

public class TierList{
    public HashMap<Integer, Tier> tiers;

    public TierList(){
         // only adds first tier in by default
        this.tiers = new HashMap<Integer, Tier>();
        this.tiers.put(0, new Tier(new ItemList(), "000,000,000", "S"));
    }
    public static void addTier(Tier tier){}
    public static void removeTier(int position){}
    public static void moveTier(int position, boolean up){}
}
