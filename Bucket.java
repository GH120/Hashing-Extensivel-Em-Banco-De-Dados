import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Bucket {

    public int             profundidade = 5;
    public int             numero;
    private String         arquivo; // Nome do arquivo que contém os registros
    private List<Registro> registros;

    public Bucket(int numero) {

        this.numero = numero;

        arquivo   =  Integer.toBinaryString(numero) + ".txt";

        registros =  new ArrayList<>();
    }

    public void carregarBucket() {

        registros.clear(); // Limpa os registros existentes

        //Cria o buffer reader logo no argumento do try para se crashar ser rápido
        try ( BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {

            String line;
            while ((line = reader.readLine()) != null) {

                // Os registros são separados por vírgula
                String[] parts = line.split(",");

                if (parts.length == 2) {

                    int linha = Integer.parseInt(parts[0]);
                    int ano   = Integer.parseInt(parts[1]);

                    Registro registro = new Registro(linha, ano);

                    registros.add(registro);
                } else {
                    System.out.println("Formato inválido de registro: " + line);
                }
            }

        }
        catch(Exception e){}
    }

    public void salvarBucket() {

        //Cria o writter no argumento do try para se crashar 
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {

            for (Registro registro : registros) {
                writer.write(registro.getNumeroLinha() + "," + registro.getAno());
                writer.newLine();
            }

        }
        catch(Exception e){}
    }

    public void limparRegistros() {
        registros.clear();
    }

    public List<Registro> getRegistros() {
        return registros;
    }

    public void adicionarRegistro(int numeroLinha, int ano) {
        registros.add(new Registro(numeroLinha, ano));
    }
}

class Registro {
    private int numeroLinha;
    private int ano;

    public Registro(int numeroLinha, int ano) {
        this.numeroLinha = numeroLinha;
        this.ano = ano;
    }

    public int getNumeroLinha() {
        return numeroLinha;
    }

    public int getAno() {
        return ano;
    }
    
}