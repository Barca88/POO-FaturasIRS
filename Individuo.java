import java.io.Serializable;
import java.util.List;

public class Individuo extends Contribuinte implements Serializable{
    private int qDepAgre;//quantidade de depententes
    List<int> lContAgre;//lista de n contribuinte do agregado
    //todo coeficente fiscal
    List<long> faturas;//todo códigos das atividades economicas
}

