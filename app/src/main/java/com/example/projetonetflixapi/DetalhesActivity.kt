package com.example.projetonetflixapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.projetonetflixapi.api.RetrofitService
import com.example.projetonetflixapi.api.RetrofitService.Companion.filmeAPI
import com.example.projetonetflixapi.databinding.ActivityDetalhesBinding
import com.example.projetonetflixapi.model.FilmeDetalhes
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class DetalhesActivity : AppCompatActivity() {

    private val TAG = "info_filme"
    private val binding by lazy {
        ActivityDetalhesBinding.inflate( layoutInflater )
    }
    private var jobFilmeDetalhes: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bundle = intent.extras
        if(bundle != null) {

//            val filme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                bundle.getParcelable("filme", Filme::class.java)
//            } else {
//                bundle.getParcelable("filme") as? Filme
//            }

            val idFilme = bundle.getInt("idFilme")

            /*if(filme != null) {
                val nomeImagem = filme.backdrop_path
                val tamanhoImagem = "w780"
                val urlImagem = RetrofitService.BASE_URL_IMAGE
                val url = urlImagem + tamanhoImagem + nomeImagem

                Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.carregando)
                    .into(binding.imgPoster)

                recuperarFilmeDetalhes(filme.id)*/

            recuperarFilmeDetalhes(idFilme)

//            }

        }else finish()

        binding.btnVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun recuperarFilmeDetalhes(id: Int) {
        jobFilmeDetalhes = CoroutineScope(Dispatchers.IO).launch {
            var resposta: Response<FilmeDetalhes>? = null

            try {
                resposta = filmeAPI.recuperarFilmeDetalhes(id)
            }catch (e: Exception) {
                e.printStackTrace()
                Log.i(TAG, "Erro ao recuperar detalhes")
                finish()
                exibirMensagem("Erro ao fazer a requisição.")
            }

            if(resposta != null) {
                if(resposta.isSuccessful) {
                    val detalhes = resposta.body()
                    val nomeImagem = detalhes?.poster_path
                    val tamanhoImagem = "w780"
                    val urlImagem = RetrofitService.BASE_URL_IMAGE
                    val url = urlImagem + tamanhoImagem + nomeImagem
                    Log.i(TAG, "Detalhes: $url")
                    withContext(Dispatchers.Main) {
                        Picasso.get()
                            .load(url)
                            .placeholder(R.drawable.carregando)
                            .error(R.drawable.capa)
                            .into(binding.imgPoster)
                        with(binding) {
                            textFilmeTitulo.text = detalhes?.title
                            textPais.text = detalhes?.production_countries?.get(0)?.name
                            textAno.text = detalhes?.release_date
                            var genero = ""
                            detalhes?.genres?.forEach {
                                genero += "${it.name}\n"
                            }
                            textGenero.text = genero
                        }
                    }

                }else {
                    exibirMensagem("Não foi possível recuperar os detalhes do filme selecionado. Código: ${resposta.code()}")
                }
            }else {
                exibirMensagem("Não foi possível fazer a requisição")
            }
        }
    }

    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(applicationContext, mensagem, Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()
        jobFilmeDetalhes?.cancel()
    }

}