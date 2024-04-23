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

    void CriarDiretorio(String arquivoDiretorio){

        diretorio = new Diretorio(profundidadeGlobal);

        //Se tiver diretorio, carrega ele
        try{
            diretorio.carregarDiretorio(arquivoDiretorio);

            profundidadeGlobal = diretorio.profundidade;
        }
        catch(Exception e){}
    }

    void inserirValor(Integer linha, Integer valor){

        int valorHash  = Hash(valor);

        int mascara = (1 << profundidadeGlobal) - 1;

        int indice = valorHash & mascara;

        var bucket = diretorio.obterBucket(indice);

        boolean cheio = bucket.getRegistros().size() >= BUCKETSIZE;

        if(cheio) 
            handleOverflow(bucket);
        else 
            bucket.adicionarRegistro(linha,valor);
    }

    void deletarValor(){

    }

    void buscarValor(){

    }

    void handleOverflow(Bucket bucket){

        //Se precisar duplicar o diretório, o faz e incrementa profundidade global
        handleDuplicaoDiretorio(bucket);
        
        //A imagem de 00 é 100, a imagem de 1001 é 11001 e assim em diante
        //O novo índice vai ser o mesmo do anterior, mas com um bit a direita
        Integer novoIndice  = bucket.numero + 1 << profundidadeGlobal;

        //Agora o ponteiro aponta para o novo bucket ao invés do antigo
        //PS: só funciona para diferenças 1 de profundidade
        // Pois se tiver 64 copias do mesmo bucket vou ter que alterar metade
        diretorio.mudarPonteiro(novoIndice, novoIndice);


        //Copio os registros do bucket para depois esvaziá-lo
        var registros = new ArrayList<>(bucket.getRegistros());
        bucket.limparRegistros();
        bucket.salvarBucket();

        //Agora para todos esses registros eu insiro eles de novo usando a inserção
        //Porque isso funciona? 
        //Pois obrigatoriamente esses valores estavam no Bucket original ou em sua imagem
        //Agora eu divido eles em dois buckets levando em conta mais um bit de profundidade

        for(Registro registro : registros){
            inserirValor(registro.linha, registro.valor);
        }
    }

    void handleDuplicaoDiretorio(Bucket bucket){

        Integer profundidadeLocal = diretorio.getProfundidade(bucket);

        if(profundidadeLocal == profundidadeGlobal){
            diretorio.duplicarDiretorio();
            profundidadeGlobal++;
        }

    }

    //Função hash a ser definida
    Integer Hash(Integer valor){
        return valor;
    }

    //Obs: Ao duplicar um bucket, salvar o original com profundidade incrementada
    //Obs: Ao criar uma imagem, lembrar de salvar ela depois
}
