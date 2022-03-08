import java.util.HashMap;
import java.util.Map;

public class Shop implements ShopInterface {

    Map<String, Integer> shopStock = new HashMap<>();
    Map<String, Object> blockade = new HashMap<>();

    private boolean isInStock(String productName) {
        boolean inStock = true;

        if (shopStock.get(productName) == null) {
            inStock = false;
        }
        return inStock;
    }

    @Override
    public void delivery(Map<String, Integer> goods) {
        for (String productName : goods.keySet()) {
            synchronized (this) {
                boolean inStock = isInStock(productName);

                if (inStock) {
                    int newQuantity = shopStock.get(productName) + goods.get(productName);
                    shopStock.put(productName, newQuantity);
                }
                if (!inStock) {
                    shopStock.put(productName, goods.get(productName));
                    blockade.computeIfAbsent(productName, k -> new Object());
                }
            }

            synchronized (blockade.get(productName)) {
                blockade.get(productName).notifyAll();
            }
        }
    }

    private boolean tryToBuy(String productName, int quantity) {
        boolean purchased = false;

        if (shopStock.get(productName) != null && shopStock.get(productName) >= quantity) {
            int newQuantity = shopStock.get(productName) - quantity;
            shopStock.put(productName, newQuantity);
            purchased = true;
        }
        return purchased;
    }

    private void wait(String productName) {
        try {
            blockade.get(productName).wait();
        } catch (InterruptedException ignored) {}
    }

    @Override
    public boolean purchase(String productName, int quantity) {
        blockade.computeIfAbsent(productName, k -> new Object());

        synchronized (blockade.get(productName)) {
            boolean purchased = tryToBuy(productName, quantity);
            if (purchased) { return true; }

            wait(productName);

            boolean tryAgain = tryToBuy(productName, quantity);
            if (tryAgain) { return true; }

            return false;
        }
    }

    @Override
    public Map<String, Integer> stock() {
        return shopStock;
    }
}
