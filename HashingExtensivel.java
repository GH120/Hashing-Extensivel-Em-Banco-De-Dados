//Implementação da maioria do trabalho
//Aceita incluir valores, deletar valores e buscar valores por igualdade
//Ao inserir um valor, vai ver seu bucket pela função hash
//Construtor aceita a profundidade global, quantidade de armazenamento no bucket e folder do diretório
//Constroi inicialmente 2² buckets vazios

import java.util.ArrayList;

public class HashingExtensivel {
    
    Integer   profundidadeGlobal;
    Diretorio diretorio;
    Integer   BUCKETSIZE = 3;
    

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

    public void deletarValor(){

    }

    public void buscarValor(){

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

        //Numero de repetições do ponteiro que apontam para esse bucket
        double step = Math.pow(2, profundidadeGlobal - profundidadeLocal);

        //Percorre o diretorio inteiro atualizando todos valores repetidos
        for(int indice = bucket.numero; indice < diretorio.getLength(); indice += step){

            //Vê no primeiro bit relevante se é original ou imagem
            boolean imagem = ((indice >> profundidadeGlobal-profundidadeLocal) & 1) == 1;

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

    //Função hash a ser definida
    private Integer Hash(Integer valor){
        return valor;
    }

    private int getIndice(int valor){
        
        int valorHash  = Hash(valor);

        int mascara = (1 << profundidadeGlobal) - 1;

        int indice = valorHash & mascara;

        return indice;
    }

    //Obs: Ao duplicar um bucket, salvar o original com profundidade incrementada
    //Obs: Ao criar uma imagem, lembrar de salvar ela depois
}
