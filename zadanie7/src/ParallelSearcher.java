public class ParallelSearcher implements ParallelSearcherInterface {

    double elementsValue = 0;

    @Override
    public void set(HidingPlaceSupplierSupplier supplier) {
        HidingPlaceSupplier lockers = supplier.get(elementsValue);

        while (lockers != null) {
            getThreads(lockers);
            lockers = supplier.get(elementsValue);
            elementsValue = 0;
        }
    }

    private void getThreads(HidingPlaceSupplier lockers) {
        int threadsNo = lockers.threads();
        Thread[] threadsArray = new Thread[threadsNo];

        for (int i = 0; i < threadsArray.length; ++i) {
            threadsArray[i] = new MyThread(this, lockers);
            threadsArray[i].start();
        }

        for (Thread thread : threadsArray) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }
    }

    class MyThread extends Thread {
        final Object sync;
        HidingPlaceSupplier lockers;

        public MyThread(Object synchronize, HidingPlaceSupplier supplier) {
            sync = synchronize;
            lockers = supplier;
        }

        @Override
        public void run() {
            HidingPlaceSupplier.HidingPlace hidingPlace = lockers.get();

            while (hidingPlace != null) {
                if (hidingPlace.isPresent()) {
                    synchronized (sync) {
                        elementsValue += hidingPlace.openAndGetValue();
                    }
                }
                hidingPlace = lockers.get();
            }
        }
    }
}
