import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Bucket {

    public  int            numero;
    private String         arquivo; // Nome do arquivo que contém os registros
    private List<Registro> registros;

    public Bucket(int numero) {

        this.numero = numero;

        arquivo   =  "arquivos/" + Integer.toBinaryString(numero) + ".txt";

        registros =  new ArrayList<>();
    }

    //É aqui que ele carrega a nova profundidade
    public Bucket carregarBucket() {

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
        catch(IOException falhouLeitura ){
            salvarBucket();
        }

        return this;
    }

    public void salvarBucket() {

        //Cria o writter no argumento do try para se crashar 
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {

            for (Registro registro : registros) {
                writer.write(registro.linha + "," + registro.ano);
                writer.newLine();
            }

        }
        catch(Exception e){}

    }

    public void deletarBucket(){

        limparRegistros();

        var arquivoTXT = new File(arquivo);

        if(!arquivoTXT.exists()){
            System.out.println("ERRO, ARQUIVO INEXISTENTE: " + arquivo);
            return;
        }

        boolean deletado = arquivoTXT.delete();

        if(!deletado){
            System.out.println("ERRO, FALHA EM DELETAR ARQUIVO: " + arquivo);
            return;
        }
    }

    public void limparRegistros() {
        registros.clear();
    }

    public List<Registro> getRegistros() {
        return registros;
    }

    public void adicionarRegistro(Registro registro) {
        registros.add(registro);
    }
}