package com.invincibles.jarvis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var output: TextView
    private lateinit var input: EditText
    private var tts: TextToSpeech? = null
    private val micRequest = 41

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        buildUi()
        say("Hello! I am Invincibles Jarvis. Type or tap the microphone to give a command.", false)
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 36, 24, 24)
            setBackgroundColor(0xFF07111F.toInt())
        }
        val title = TextView(this).apply {
            text = "INVINCIBLES JARVIS"
            textSize = 26f
            gravity = Gravity.CENTER
            setTextColor(0xFF66D9FF.toInt())
            setPadding(0, 10, 0, 22)
        }
        output = TextView(this).apply {
            text = "Jarvis ready.\n"
            textSize = 18f
            setTextColor(0xFFEAF6FF.toInt())
            setPadding(16, 16, 16, 16)
        }
        val scroll = ScrollView(this).apply { addView(output) }
        input = EditText(this).apply {
            hint = "Type a command..."
            setHintTextColor(0xFF91A7B8.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setSingleLine(true)
        }
        val send = Button(this).apply { text = "Run command"; setOnClickListener { runCommand(input.text.toString()) } }
        val mic = Button(this).apply { text = "🎙 Speak"; setOnClickListener { startListening() } }
        val examples = TextView(this).apply {
            text = "Try: time • date • open YouTube • search cats • help"
            textSize = 13f
            setTextColor(0xFF9EB4C7.toInt())
            setPadding(0, 12, 0, 0)
        }
        root.addView(title, matchWrap())
        root.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        root.addView(input, matchWrap())
        root.addView(send, matchWrap())
        root.addView(mic, matchWrap())
        root.addView(examples, matchWrap())
        setContentView(root)
    }

    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            respond("Speech recognition is not available on this device."); return
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), micRequest); return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Invincibles Jarvis")
        }
        try { startActivityForResult(intent, 42) } catch (_: Exception) { respond("Could not start speech input.") }
    }

    @Deprecated("Deprecated by Android, retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 42 && resultCode == RESULT_OK) {
            val words = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            input.setText(words)
            runCommand(words)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == micRequest && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) startListening()
        else respond("Microphone permission is needed for voice commands.")
    }

    private fun runCommand(raw: String) {
        val command = raw.trim()
        if (command.isBlank()) { respond("Please enter a command."); return }
        val lower = command.lowercase(Locale.ROOT)
        when {
            lower in listOf("hi", "hello", "salam", "assalamualaikum") -> respond("Hello! How can I help you?")
            "time" in lower -> respond("The time is ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())}.")
            "date" in lower -> respond("Today is ${SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date())}.")
            lower == "help" || "what can you do" in lower -> respond("I can tell the time and date, open YouTube, search the web, and open Android settings. More actions can be added in future versions.")
            lower.startsWith("open youtube") || lower == "youtube" -> openUrl("https://www.youtube.com")
            lower.startsWith("open google") -> openUrl("https://www.google.com")
            lower.startsWith("search ") -> openUrl("https://www.google.com/search?q=" + android.net.Uri.encode(command.removePrefix("search ")))
            lower.startsWith("open settings") || lower == "settings" -> try { startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)); respond("Opening settings.") } catch (_: Exception) { respond("Could not open settings.") }
            else -> respond("I heard: $command. This command is not built in yet. Say help to see supported commands.")
        }
        input.setText("")
    }

    private fun openUrl(url: String) {
        try { startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))); respond("Opening now.") }
        catch (_: Exception) { respond("Could not open that link.") }
    }

    private fun respond(message: String) = say(message, true)
    private fun say(message: String, speak: Boolean) {
        if (::output.isInitialized) output.append("\nJarvis: $message\n")
        if (speak) tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "jarvis_reply")
    }
    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) tts?.language = Locale.getDefault() }
    override fun onDestroy() { tts?.stop(); tts?.shutdown(); super.onDestroy() }
}
