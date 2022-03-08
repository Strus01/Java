import java.util.List;
import java.util.ArrayList;

class Loops implements GeneralLoops {

    List<Integer> lower_limit = new ArrayList<Integer>();
    List<Integer> upper_limit = new ArrayList<Integer>();

    @Override
    public void setLowerLimits(List<Integer> limits) {
        lower_limit = limits;
    }

    @Override
    public void setUpperLimits(List<Integer> limits) {
        upper_limit = limits;
    }

    @Override
    public List<List<Integer>> getResult() {

        var result = new ArrayList<List<Integer>>();

        if(lower_limit.size() == 0) {
            lower_limit.add(0);
        }

        if(upper_limit.size() == 0) {
            upper_limit.add(0);
        }

        if(lower_limit.size() != upper_limit.size()) {
            while(lower_limit.size() > upper_limit.size()) {
                upper_limit.add(0);
            }
            while(upper_limit.size() > lower_limit.size()) {
                lower_limit.add(0);
            }
        }

        recursion(null, result, 0);

        return result;
    }
    private void recursion(List<Integer> tmp, List<List<Integer>> result, int index) {
        if(index == lower_limit.size()) {
            result.add(tmp);
            return;
        }
        for(int i = lower_limit.get(index); i <= upper_limit.get(index) ; i++) {
            var r_iter = tmp != null ? new ArrayList<Integer>(tmp) : new ArrayList<Integer>();
            r_iter.add(i);
            recursion(r_iter, result, index + 1);
        }
    }
}