package com.carpool.app.feature.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.carpool.app.core.designsystem.*

data class ChatMessage(val text: String, val time: String, val outgoing: Boolean = false, val day: String = "Today")
data class DemoConversation(
    val id: String, val name: String, val initials: String, val role: String,
    val route: String, val departure: String, val unread: Int,
    val messages: List<ChatMessage>
)

class DemoChatState {
    val conversations = mutableStateListOf(
        DemoConversation("ananya", "Ananya Rao", "AR", "Your driver", "Indiranagar ? Whitefield", "Tomorrow · 8:30 AM", 2, listOf(
            ChatMessage("Hi! Thanks for joining my morning ride. Does the Indiranagar metro pickup work for you?", "6:42 PM", day = "Yesterday"),
            ChatMessage("Hi Ananya! Yes, I'll wait by the entrance near Gate B.", "6:45 PM", true, "Yesterday"),
            ChatMessage("Perfect. It's a blue Hyundai i20. There's room for your backpack too.", "6:46 PM", day = "Yesterday"),
            ChatMessage("Thanks! Looking forward to a calmer commute.", "6:48 PM", true, "Yesterday"),
            ChatMessage("Good morning! Our pickup is confirmed for tomorrow at 8:30.", "9:12 AM"),
            ChatMessage("Let's meet at Gate B. See you there!", "9:14 AM")
        )),
        DemoConversation("rahul", "Rahul Mehta", "RM", "Fellow rider", "Koramangala ? Electronic City", "Tomorrow · 9:00 AM", 1, listOf(
            ChatMessage("Hey, are you also joining the Electronic City ride?", "8:50 AM"),
            ChatMessage("Yes! I booked a seat for tomorrow.", "8:52 AM", true),
            ChatMessage("Nice! I usually grab a coffee near the pickup point.", "8:54 AM"),
            ChatMessage("Sounds good. I'll arrive a few minutes early.", "8:56 AM", true),
            ChatMessage("Great, see you at the cafe entrance.", "9:02 AM")
        )),
        DemoConversation("priya", "Priya Sharma", "PS", "Your driver", "HSR Layout ? Manyata Tech Park", "Yesterday · 8:00 AM", 0, listOf(
            ChatMessage("I'm at the pickup point, next to the park.", "7:55 AM", day = "Yesterday"),
            ChatMessage("On my way! I'll be there in two minutes.", "7:56 AM", true, "Yesterday"),
            ChatMessage("No rush. I'll wait here.", "7:57 AM", day = "Yesterday"),
            ChatMessage("Thanks for the ride, Priya. That was really comfortable!", "9:10 AM", true, "Yesterday"),
            ChatMessage("You're welcome! Happy to share the ride again.", "9:15 AM", day = "Yesterday")
        ))
    )

    fun markRead(id: String) {
        val index = conversations.indexOfFirst { it.id == id }
        if (index >= 0) conversations[index] = conversations[index].copy(unread = 0)
    }

    fun send(id: String, text: String) {
        val index = conversations.indexOfFirst { it.id == id }
        if (index < 0 || text.isBlank()) return
        val chat = conversations[index]
        conversations[index] = chat.copy(messages = chat.messages + ChatMessage(
            text.trim(), LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")), outgoing = true
        ))
    }
}

@Composable
fun ChatInbox(state: DemoChatState, open: (String) -> Unit, modifier: Modifier = Modifier) {
    var query by rememberSaveable { mutableStateOf("") }
    val chats = state.conversations.filter {
        it.name.contains(query, true) || it.route.contains(query, true)
    }
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("Inbox", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(8.dp))
            Text("Good journeys begin with a hello.", color = Muted)
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search people or routes") }, singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Search, null) }, shape = RoundedCornerShape(16.dp))
        }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Your conversations", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text("DEMO", color = Forest, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        if (chats.isEmpty()) item { Text("No conversations match your search.", color = Muted) }
        items(chats, key = { it.id }) { chat ->
            Surface(shape = RoundedCornerShape(22.dp), color = Color.White,
                modifier = Modifier.fillMaxWidth().clickable { state.markRead(chat.id); open(chat.id) }) {
                Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Avatar(chat.initials)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(chat.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(chat.messages.last().time, fontSize = 10.sp, color = Muted)
                        }
                        Text(chat.route, fontSize = 11.sp, color = Forest, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val last = chat.messages.last()
                            Text((if (last.outgoing) "You: " else "") + last.text, fontSize = 13.sp,
                                color = Muted, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            if (chat.unread > 0) {
                                Spacer(Modifier.width(8.dp))
                                Surface(color = Forest, shape = CircleShape) {
                                    Text(chat.unread.toString(), Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
        item { Text("Sample conversations · messages stay in this demo session.", color = Muted, fontSize = 12.sp) }
    }
}

@Composable
fun ChatConversation(state: DemoChatState, id: String, back: () -> Unit, modifier: Modifier = Modifier) {
    val chat = state.conversations.firstOrNull { it.id == id } ?: return
    var draft by rememberSaveable(id) { mutableStateOf("") }
    val listState = rememberLazyListState()
    BackHandler(onBack = back)
    LaunchedEffect(id, chat.messages.size) {
        listState.animateScrollToItem(chat.messages.lastIndex)
    }
    Column(modifier.fillMaxSize().imePadding()) {
        Surface(color = Color.White) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = back) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back to inbox") }
                Avatar(chat.initials)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(chat.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(chat.role, color = Muted, fontSize = 12.sp)
                }
            }
        }
        Surface(color = Color(0xFFEAF0DF), shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.DirectionsCar, null, tint = Forest)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(chat.route, color = Forest, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(chat.departure, color = Muted, fontSize = 12.sp)
                }
            }
        }
        LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(chat.messages.size) { index ->
                val message = chat.messages[index]
                Column {
                    if (index == 0 || chat.messages[index - 1].day != message.day) {
                        Text(message.day, color = Muted, fontSize = 11.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 12.dp))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (message.outgoing) Arrangement.End else Arrangement.Start) {
                        Surface(color = if (message.outgoing) Forest else Color.White,
                            shape = RoundedCornerShape(18.dp, 18.dp, if (message.outgoing) 4.dp else 18.dp, if (message.outgoing) 18.dp else 4.dp),
                            modifier = Modifier.fillMaxWidth(0.84f)) {
                            Column(Modifier.padding(14.dp)) {
                                Text(message.text, color = if (message.outgoing) Color.White else Ink, fontSize = 14.sp, lineHeight = 21.sp)
                                Spacer(Modifier.height(6.dp))
                                Row(Modifier.align(Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                                    Text(message.time, color = if (message.outgoing) Lime else Muted, fontSize = 10.sp)
                                    if (message.outgoing) {
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.Outlined.Check, "Saved locally", tint = Lime, modifier = Modifier.size(13.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Surface(color = Color.White) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(draft, { draft = it }, placeholder = { Text("Write a message…") },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp), maxLines = 4)
                    FilledIconButton(onClick = { state.send(id, draft); draft = "" }, enabled = draft.isNotBlank()) {
                        Icon(Icons.AutoMirrored.Outlined.Send, "Send message")
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text("Demo chat · messages are saved locally for this session", fontSize = 10.sp, color = Muted)
            }
        }
    }
}

@Composable
private fun Avatar(initials: String) {
    Box(Modifier.size(46.dp).background(Lime, CircleShape), contentAlignment = Alignment.Center) {
        Text(initials, color = Forest, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
