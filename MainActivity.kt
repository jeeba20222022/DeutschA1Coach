package com.jeeba.deutscha1coach

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class Word(val de: String, val fr: String)
private data class Quiz(val question: String, val choices: List<String>, val answer: Int)
private data class Lesson(
    val page: Int,
    val title: String,
    val objective: String,
    val summary: String,
    val words: List<Word>,
    val quiz: List<Quiz>
)

// Contenu de démonstration : explications originales et non reproduction intégrale du manuel.
private val lessons = listOf(
    Lesson(
        page = 14,
        title = "Internationale Wörter",
        objective = "Reconnaître des mots internationaux et comprendre leur sens grâce au contexte.",
        summary = "La page illustrée présente des mots proches entre plusieurs langues et des personnes venant de pays différents. L'objectif est de repérer les mots que l'on peut déjà comprendre sans tout traduire.",
        words = listOf(
            Word("Hotel", "hôtel"), Word("Musik", "musique"), Word("Universität", "université"),
            Word("Restaurant", "restaurant"), Word("Taxi", "taxi"), Word("Konzert", "concert"),
            Word("Job", "emploi / travail"), Word("Internet", "internet")
        ),
        quiz = listOf(
            Quiz("Quel mot allemand correspond à « musique » ?", listOf("Musik", "Hotel", "Taxi", "Job"), 0),
            Quiz("Quel mot signifie « université » ?", listOf("Konzert", "Universität", "Restaurant", "Internet"), 1),
            Quiz("« Hotel » est-il facile à reconnaître ?", listOf("Oui", "Non"), 0)
        )
    ),
    Lesson(
        page = 16,
        title = "Treffen im Café — entraînement",
        objective = "Se présenter, demander l'origine d'une personne et parler simplement d'une boisson.",
        summary = "Étape d'entraînement construite à partir des éléments travaillés précédemment avec l'apprenant. Elle sert de modèle pour le parcours page par page.",
        words = listOf(
            Word("Woher kommst du?", "Tu viens d'où ?"),
            Word("Woher kommen Sie?", "Vous venez d'où ? — formel"),
            Word("Ich komme aus Benin.", "Je viens du Bénin."),
            Word("Was trinkst du?", "Qu'est-ce que tu bois ?"),
            Word("Kaffee oder Tee?", "Café ou thé ?")
        ),
        quiz = listOf(
            Quiz("« Ich komme aus Benin. » signifie…", listOf("Je vais au Bénin.", "Je viens du Bénin.", "J'habite en Allemagne."), 1),
            Quiz("Comment demander « Tu viens d'où ? » ?", listOf("Was trinkst du?", "Woher kommst du?", "Guten Tag?"), 1),
            Quiz("« Kaffee oder Tee? » parle de…", listOf("la ville", "la boisson", "la profession"), 1)
        )
    )
)

private class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("progress", Context.MODE_PRIVATE)
    fun isDone(page: Int) = prefs.getBoolean("page_$page", false)
    fun setDone(page: Int, value: Boolean) = prefs.edit().putBoolean("page_$page", value).apply()
    fun reset() = prefs.edit().clear().apply()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DeutschA1CoachApp(this) }
    }
}

@Composable
private fun DeutschA1CoachApp(context: Context) {
    val store = remember { ProgressStore(context) }
    var screen by remember { mutableStateOf("home") }
    var selected by remember { mutableStateOf(0) }
    var refresh by remember { mutableIntStateOf(0) }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(screen == "home", { screen = "home" }, icon = { Text("⌂") }, label = { Text("Accueil") })
                    NavigationBarItem(screen == "course", { screen = "course" }, icon = { Text("📘") }, label = { Text("Cours") })
                    NavigationBarItem(screen == "review", { screen = "review" }, icon = { Text("↻") }, label = { Text("Révision") })
                    NavigationBarItem(screen == "settings", { screen = "settings" }, icon = { Text("⚙") }, label = { Text("Réglages") })
                }
            }
        ) { pad ->
            when (screen) {
                "home" -> HomeScreen(Modifier.padding(pad), store, refresh) { screen = "course" }
                "course" -> CourseList(Modifier.padding(pad), store, refresh) { selected = it; screen = "lesson" }
                "lesson" -> LessonScreen(Modifier.padding(pad), lessons[selected], store) { refresh++; screen = "course" }
                "review" -> ReviewScreen(Modifier.padding(pad), store) { selected = it; screen = "lesson" }
                else -> SettingsScreen(Modifier.padding(pad), store) { refresh++; screen = "home" }
            }
        }
    }
}

@Composable
private fun HomeScreen(modifier: Modifier, store: ProgressStore, refresh: Int, openCourse: () -> Unit) {
    val done = lessons.count { store.isDone(it.page) }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Deutsch A1 – Mein Coach", style = MaterialTheme.typography.headlineMedium)
        Text("Ton accompagnateur hors ligne", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(20.dp))
        Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) {
            Text("Ta progression", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(progress = { done.toFloat() / lessons.size.coerceAtLeast(1) }, Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Text("$done / ${lessons.size} étapes validées")
        }}
        Spacer(Modifier.height(16.dp))
        Button(onClick = openCourse, Modifier.fillMaxWidth()) { Text("▶ Continuer le cours") }
        Spacer(Modifier.height(12.dp))
        Text("Méthode", style = MaterialTheme.typography.titleLarge)
        Text("Comprendre → vocabulaire → explication → entraînement → quiz → validation → révision.")
        Spacer(Modifier.height(12.dp))
        Text("100 % hors ligne : la progression est enregistrée sur le téléphone.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun CourseList(modifier: Modifier, store: ProgressStore, refresh: Int, open: (Int) -> Unit) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Parcours A1", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(lessons.indices.toList()) { i ->
                val l = lessons[i]
                Card(Modifier.fillMaxWidth().clickable { open(i) }) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Page ${l.page}", style = MaterialTheme.typography.labelLarge)
                            Text(l.title, style = MaterialTheme.typography.titleMedium)
                            Text(l.objective, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(if (store.isDone(l.page)) "✓" else "›", style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonScreen(modifier: Modifier, lesson: Lesson, store: ProgressStore, close: () -> Unit) {
    var quizStarted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var current by remember { mutableIntStateOf(0) }
    var answered by remember { mutableStateOf(false) }
    var selectedChoice by remember { mutableIntStateOf(-1) }
    var flash by remember { mutableStateOf(false) }

    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Text("Page ${lesson.page}", style = MaterialTheme.typography.labelLarge)
        Text(lesson.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Text("🎯 Objectif", style = MaterialTheme.typography.titleMedium)
        Text(lesson.objective)
        Spacer(Modifier.height(14.dp))
        Text("📖 Comprendre", style = MaterialTheme.typography.titleMedium)
        Text(lesson.summary)
        Spacer(Modifier.height(16.dp))
        Text("🧠 Vocabulaire", style = MaterialTheme.typography.titleMedium)
        lesson.words.forEach { w ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text(w.de, style = MaterialTheme.typography.titleMedium)
                    Text(w.fr)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = { flash = !flash }, Modifier.fillMaxWidth()) { Text(if (flash) "Masquer les cartes" else "🃏 Mode flashcards") }
        if (flash) lesson.words.shuffled().take(3).forEach { w ->
            Text("• ${w.de}  →  ${w.fr}", Modifier.padding(vertical = 3.dp))
        }
        Spacer(Modifier.height(12.dp))
        if (!quizStarted) {
            Button(onClick = { quizStarted = true; current = 0; score = 0; answered = false; selectedChoice = -1 }, Modifier.fillMaxWidth()) { Text("Commencer le quiz") }
        } else if (current < lesson.quiz.size) {
            val q = lesson.quiz[current]
            Text("Quiz ${current + 1}/${lesson.quiz.size}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp)); Text(q.question)
            q.choices.forEachIndexed { i, c ->
                Button(onClick = { if (!answered) { selectedChoice = i; answered = true; if (i == q.answer) score++ } }, Modifier.fillMaxWidth().padding(vertical = 3.dp)) { Text(c) }
            }
            if (answered) {
                Text(if (selectedChoice == q.answer) "✅ Bonne réponse" else "❌ Réponse à revoir", Modifier.padding(vertical = 6.dp))
                Button(onClick = { current++; answered = false; selectedChoice = -1 }, Modifier.fillMaxWidth()) { Text("Question suivante") }
            }
        } else {
            Text("Score : $score / ${lesson.quiz.size}", style = MaterialTheme.typography.titleLarge)
            val passed = score >= (lesson.quiz.size + 1) / 2
            Text(if (passed) "🎉 Étape validée. Continue ainsi !" else "🔄 Recommence la révision avant de valider.")
            Spacer(Modifier.height(8.dp))
            Button(onClick = { store.setDone(lesson.page, passed); close() }, Modifier.fillMaxWidth()) { Text(if (passed) "Valider la page" else "Retour au cours") }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = close, Modifier.fillMaxWidth()) { Text("← Retour au parcours") }
    }
}

@Composable
private fun ReviewScreen(modifier: Modifier, store: ProgressStore, open: (Int) -> Unit) {
    val pending = lessons.indices.filter { !store.isDone(lessons[it].page) }
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Révision", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (pending.isEmpty()) Text("🎉 Toutes les étapes actuelles sont validées.")
        else {
            Text("Étapes à revoir :")
            pending.forEach { i ->
                OutlinedButton(onClick = { open(i) }, Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text("Page ${lessons[i].page} — ${lessons[i].title}") }
            }
        }
    }
}

@Composable
private fun SettingsScreen(modifier: Modifier, store: ProgressStore, refreshHome: () -> Unit) {
    var confirm by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize().padding(18.dp)) {
        Text("Réglages", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text("Deutsch A1 – Mein Coach v0.2.0")
        Spacer(Modifier.height(12.dp))
        Text("Mode hors ligne activé. Aucun compte n'est nécessaire.")
        Spacer(Modifier.height(20.dp))
        Button(onClick = { confirm = true }, Modifier.fillMaxWidth()) { Text("Réinitialiser la progression") }
    }
    if (confirm) AlertDialog(
        onDismissRequest = { confirm = false },
        title = { Text("Réinitialiser ?") },
        text = { Text("Toutes les validations locales seront supprimées.") },
        confirmButton = { TextButton(onClick = { store.reset(); confirm = false; refreshHome() }) { Text("Oui") } },
        dismissButton = { TextButton(onClick = { confirm = false }) { Text("Annuler") } }
    )
}
