package com.example.projetonetflixapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetonetflixapi.adapter.PesquisaAdapter
import com.example.projetonetflixapi.api.FilmeAPI
import com.example.projetonetflixapi.api.RetrofitService
import com.example.projetonetflixapi.databinding.ActivityPesquisaBinding
import com.example.projetonetflixapi.model.FilmeResposta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class PesquisaActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPesquisaBinding.inflate(layoutInflater)
    }
    private var pesquisaAdapter: PesquisaAdapter? = null
    private val TAG = "info_filme"
    private var jobPesquisar: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        pesquisaAdapter = PesquisaAdapter{
            val intent = Intent(this, DetalhesActivity::class.java)
            intent.putExtra("idFilme", it.id)
            Log.i(TAG, "idFilme: ${it.id} - Título: ${it.title}")
//            intent.putExtra("filme", it)
            startActivity(intent)
        }
        binding.rvPesquisa.adapter = pesquisaAdapter

        binding.rvPesquisa.layoutManager = LinearLayoutManager(this)

        val nomeFilme = intent.extras?.getString("nome")

        if(nomeFilme != null)
        pesquisarFilme(nomeFilme)
    }

    private suspend fun recuperarFilmePesquisa(nome: String) {
        var resposta: Response<FilmeResposta>? = null

        try {
            val filmesAPI = RetrofitService.retrofit.create(FilmeAPI::class.java)
            resposta = filmesAPI.recuperarFilmePesquisa(nome)
        }catch (e: Exception) {
            e.printStackTrace()
            Log.i(TAG, "Erro ao pesquisar filme")
        }

        if(resposta != null && resposta.isSuccessful) {
            val retorno = resposta.body()
            val listaFilmes = retorno?.results

            listaFilmes?.forEach {
                Log.i(TAG, "${it.title} - ${it.id}")
             }
            withContext(Dispatchers.Main) {
                if (listaFilmes != null && listaFilmes.isNotEmpty()) {
                    pesquisaAdapter?.adicionarLista(listaFilmes)
                }
            }
        }else {
            Log.i(TAG, "Erro na requisição: ${resposta?.code()} - ${resposta?.message()}")
        }

    }

    private fun pesquisarFilme(nome: String) {
        Log.i(TAG, "pesquisarFilme: $nome")
        jobPesquisar = CoroutineScope(Dispatchers.IO).launch {
            recuperarFilmePesquisa(nome)
        }
    }

    override fun onStop() {
        super.onStop()
        jobPesquisar?.cancel()
        finish()
    }
}