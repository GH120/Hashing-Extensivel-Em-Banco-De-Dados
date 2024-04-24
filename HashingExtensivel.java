//Implementação da maioria do trabalho
//Aceita incluir valores, deletar valores e buscar valores por igualdade
//Ao inserir um valor, vai ver seu bucket pela função hash
//Construtor aceita a profundidade global, quantidade de armazenamento no bucket e folder do diretório
//Constroi inicialmente 2² buckets vazios

import java.util.*;
import java.util.stream.Collectors;

public class HashingExtensivel {
    
    int   profundidadeGlobal;
    Diretorio diretorio;
    int   BUCKETSIZE = 3;
    

    HashingExtensivel(int profundidadeGlobal){

        this.profundidadeGlobal = profundidadeGlobal;

        this.CriarDiretorio("diretorio");

    }

    public void inserirValor(Integer linha, Integer valor){

        var bucket = diretorio.obterBucket(getIndice(valor));

        bucket.carregarBucket();

        boolean cheio = bucket.getRegistros().size() >= BUCKETSIZE;

        if(cheio) 
            handleOverflow(bucket,linha,valor);
        else {
            bucket.adicionarRegistro(linha,valor);
            bucket.salvarBucket();
        }
        
    }

    public void deletarValor(Integer valor){

        var bucket = diretorio.obterBucket(getIndice(valor));

        bucket.carregarBucket();

        bucket.getRegistros().removeIf(registro -> registro.valor == valor);

        bucket.salvarBucket();

        handleReduzirDiretorio(bucket);
    }

    public List<Registro> buscarValor(Integer valor){

        var bucket = diretorio.obterBucket(getIndice(valor));

        bucket.carregarBucket();

        return bucket.getRegistros()
                     .stream()
                     .filter(registro -> registro.valor == valor)
                     .collect(Collectors.toList());
    }

    private void CriarDiretorio(String arquivoDiretorio){

        diretorio = new Diretorio(profundidadeGlobal);

        //Se tiver diretorio, carrega ele
        try{
            diretorio.carregarDiretorio(arquivoDiretorio);

            profundidadeGlobal = diretorio.profundidade;
        }
        catch(Exception e){
            diretorio = new Diretorio(profundidadeGlobal);

            
        }
    }

    private void handleOverflow(Bucket bucket, int linha, int valor){

        int profundidadeLocal = diretorio.getProfundidade(bucket);

        //Se precisar duplicar o diretório, o faz e incrementa profundidade global
        if(profundidadeLocal == profundidadeGlobal){
            diretorio.duplicarDiretorio();
            profundidadeGlobal++;
        }
        
        //A imagem de 00 é 100, a imagem de 1001 é 11001 e assim em diante
        Integer bucketImagem  = bucket.numero + (1 << profundidadeLocal);

        //O step entre cada índice que apontava para o bucket original
        double step = Math.pow(2, profundidadeLocal);

        //Percorre o diretorio atualizando ponteiros e profundidades desses buckets
        for(int indice = bucket.numero; indice < diretorio.getLength(); indice += step){

            //Vê no primeiro bit relevante se é original ou não imagem
            boolean imagem = ((indice >> profundidadeLocal) & 1) == 1;

            //Agora o ponteiro aponta para o novo bucket ao invés do antigo
            if(imagem) diretorio.mudarPonteiro(indice, bucketImagem);

            diretorio.incrementarProfundidadeLocal(indice);
        }


        //Copio os registros do bucket para depois esvaziá-lo
        var registros = new ArrayList<>(bucket.getRegistros());
        bucket.limparRegistros();
        bucket.salvarBucket();

        //O novo valor a ser inserido além dos que já existem
        registros.add(new Registro(linha, valor));

        //Reinsere os registros agora dividindo entre os dois buckets
        for(Registro registro : registros){
            inserirValor(registro.linha, registro.valor);
        }
    }



    /** Para caso a profundidade global seja maior que todas as locais
     * e seja desnecessário manter ponteiros redundantes agora que esse bucket está vazio**/
    private void handleReduzirDiretorio(Bucket bucket){

    }

    
    /**Função hash a ser definida */
    private Integer Hash(Integer valor){
        return valor;
    }

    //**Transforma valor em um indice do diretório usando o hash dele e sua máscara */
    private int getIndice(int valor){
        
        int valorHash  = Hash(valor);

        int mascara = (1 << profundidadeGlobal) - 1;

        int indice = valorHash & mascara;

        return indice;
    }

    //Obs: Ao duplicar um bucket, salvar o original com profundidade incrementada
    //Obs: Ao criar uma imagem, lembrar de salvar ela depois
}
