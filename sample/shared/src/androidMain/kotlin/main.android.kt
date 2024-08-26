import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable fun MainView(
) {
    val _context = LocalContext.current
    App(_context)
}
