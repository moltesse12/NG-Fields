# Flutter — Référence packages NG-Fields

> **⚠️ Non démarré** — Le répertoire `mobile/` est vide. Ce document est une référence pour le futur.

---

## 1. Riverpod (State Management)

**Site** : https://riverpod.dev/
**Pub** : `flutter_riverpod` (Flutter), `riverpod` (Dart only)
**Version** : ^3.x

### Providers clés
| Provider | Usage |
|----------|-------|
| `Provider<T>` | Valeur simple (service, config) |
| `StateNotifierProvider<Notifier, State>` | État mutable (auth, formulaire) |
| `FutureProvider<T>` | Requête asynchrone (liste clients) |
| `StreamProvider<T>` | Stream (sync status, WebSocket) |

### Patterns utilisés dans le projet
```dart
// 1. Provider simple (injection dépendance)
final apiServiceProvider = Provider<ApiService>((ref) => ApiService());

// 2. StateNotifier (état auth)
final authStateProvider = StateNotifierProvider<AuthStateNotifier, AuthState>((ref) {
  return AuthStateNotifier(ref.watch(authServiceProvider));
});

// 3. FutureProvider (données API)
final clientListProvider = FutureProvider<List<dynamic>>((ref) async {
  final api = ref.watch(apiServiceProvider);
  final response = await api.get('/api/clients');
  return response.data as List<dynamic>;
});

// 4. ConsumerWidget vs ConsumerStatefulWidget
class MyScreen extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final data = ref.watch(myProvider);
    // ...
  }
}
```

---

## 2. GoRouter (Navigation)

**Pub** : `go_router`
**Version** : ^17.x

### Routes déclaratives
```dart
final router = GoRouter(
  initialLocation: '/login',
  redirect: (context, state) {
    // Vérifier auth et rediriger
    final isLoggedIn = ...;
    final isLoginRoute = state.matchedLocation == '/login';
    if (!isLoggedIn && !isLoginRoute) return '/login';
    if (isLoggedIn && isLoginRoute) return '/dashboard';
    return null; // pas de redirection
  },
  routes: [
    GoRoute(path: '/login', builder: (_, __) => const LoginScreen()),
    GoRoute(path: '/dashboard', builder: (_, __) => const DashboardScreen()),
    GoRoute(
      path: '/intervention/:id',
      builder: (_, state) => InterventionFormScreen(
        interventionId: state.pathParameters['id'],
      ),
    ),
  ],
);
```

### Navigation
```dart
context.push('/intervention/new');      // pousser une route
context.pop(result);                     // retour avec résultat
context.go('/dashboard');                // remplacer toute la pile
context.pushReplacement('/login');       // remplacer page courante
```

---

## 3. Drift (Base locale SQLite)

**Pub** : `drift` + `drift/native` + `sqlite3_flutter_libs`
**Version** : ^2.x

### Définition table
```dart
@DriftDatabase(tables: [Interventions, Clients, SyncMeta])
class AppDatabase extends _$AppDatabase {
  AppDatabase() : super(_openConnection());
}
```

### Opérations CRUD
```dart
// INSERT
await into(interventions).insert(InterventionsCompanion(
  localId: Value('LOCAL-123'),
  clientId: Value('client-uuid'),
  // ...
));

// SELECT
final rows = await (select(interventions)..where((t) => t.synced.equals(false))).get();

// UPDATE
await (update(interventions)..where((t) => t.localId.equals(id)))
    .write(const InterventionsCompanion(synced: Value(true)));

// DELETE
await (delete(interventions)..where((t) => t.localId.equals(id))).go();
```

---

## 4. Dio (HTTP Client)

**Pub** : `dio`
**Version** : ^5.x

### Configuration avec interceptor JWT
```dart
final dio = Dio(BaseOptions(baseUrl: AppConstants.apiBaseUrl));
dio.interceptors.add(InterceptorsWrapper(
  onRequest: (options, handler) {
    options.headers['Authorization'] = 'Bearer $token';
    handler.next(options);
  },
  onError: (error, handler) {
    if (error.response?.statusCode == 401) {
      // refresh token ou logout
    }
    handler.next(error);
  },
));
```

---

## 5. Packages complémentaires

| Package | Usage | Version |
|---------|-------|---------|
| `image_picker` | Caméra + galerie | ^1.x |
| `signature` | Canvas signature | ^5.x |
| `geolocator` | GPS | ^12.x |
| `flutter_secure_storage` | Stockage token JWT | ^9.x |
| `path_provider` | Chemins fichiers locaux | ^2.x |
| `share_plus` | Partage de fichiers | ^10.x |

---

## Références officielles

- Flutter docs : https://docs.flutter.dev/
- Riverpod docs : https://riverpod.dev/docs
- GoRouter docs : https://pub.dev/documentation/go_router/latest/
- Drift docs : https://drift.simonbinder.eu/docs/
- Dio docs : https://pub.dev/packages/dio
- Pub.dev (tous les packages) : https://pub.dev/
