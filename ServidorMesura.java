import java.text.DecimalFormat;

public class ServidorMesura {
    String name;
    double prvValue;
    double nxtValue;
    double min;
    double max;
    
    ServidorMesura (String name, double min, double max) {
        this.name = name;
        this.prvValue = min;
        this.nxtValue = 0;
        this.min = min;
        this.max = max;
        this.setValueRandom();
    }

    void setValueRandom () {
        double random = ((Math.random() * (this.max - this.min)) + this.min);
        this.prvValue = this.nxtValue;
        this.nxtValue = random;
    }

    double getValue (double percentage) {
        double result;
        result = prvValue + ((percentage * (nxtValue - prvValue)) / 100);
        return result;
    }
    
    String getValueString (double percentage) {
        double value = this.getValue(percentage);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return df.format(value);
    }

    String toString (double percentage) {
        return this.name+"="+this.getValueString(percentage);
    }
}
