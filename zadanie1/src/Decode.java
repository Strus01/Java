class Decode extends DecoderInterface {

    private int num[] = new int[] {-1, -1, -1, -1};
    private int i;
    private String result = "";


    @Override
    public void input(int bit) {
        if (bit == 1) {
            i++;
        } else {
            if (i != 0) {
                if (num[0] == -1) {
                    num[0] = i;
                    num[1] = 2 * i;
                    num[2] = 3 * i;
                    num[3] = 4 * i;
                }

                for (int j = 0; j < 4; j++) {
                    if (num[j] == i) {
                        result = result + j;
                    }
                }
                i = 0;
            }
        }
    }

    @Override
    public String output() {
        return result;
    }

    @Override
    public void reset() {
        result = "";
        num[0] = -1;
        num[1] = -1;
        num[2] = -1;
        num[3] = -1;

    }

}
