<aside class="w-72 bg-slate-900 text-slate-200 flex flex-col min-h-screen shadow-xl">

    <!-- Logo -->
    <div class="h-16 border-b border-slate-800 flex items-center justify-center">
        <h1 class="text-2xl font-bold tracking-wide">
            Reply<span class="text-red-500">Pro</span>
        </h1>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 px-4 py-6 space-y-2">

        <!-- Dashboard -->
        <a href="{{ route('dashboard') }}"
           class="flex items-center px-4 py-3 rounded-lg transition {{ request()->routeIs('dashboard') ? 'bg-red-600 text-white shadow-lg' : 'hover:bg-slate-800 text-slate-300' }}">
            <span class="text-xl mr-3">📊</span>
            Dashboard
        </a>

        <!-- Clients -->
        <a href="{{ route('clients.index') }}"
           class="flex items-center px-4 py-3 rounded-lg transition {{ request()->routeIs('clients.*') ? 'bg-red-600 text-white shadow-lg' : 'hover:bg-slate-800 text-slate-300' }}">
            <span class="text-xl mr-3">👥</span>
            Clients
        </a>

        <!-- Activations -->
        <a href="{{ route('activations.index') }}"
           class="flex items-center px-4 py-3 rounded-lg transition {{ request()->routeIs('activations.*') ? 'bg-red-600 text-white shadow-lg' : 'hover:bg-slate-800 text-slate-300' }}">
            <span class="text-xl mr-3">✅</span>
            Activations
        </a>

        <!-- Reports -->
        <a href="#"
           class="flex items-center px-4 py-3 rounded-lg transition hover:bg-slate-800 text-slate-300">
            <span class="text-xl mr-3">📈</span>
            Reports
        </a>

        <!-- Settings -->
        <a href="#"
           class="flex items-center px-4 py-3 rounded-lg transition hover:bg-slate-800 text-slate-300">
            <span class="text-xl mr-3">⚙️</span>
            Settings
        </a>

    </nav>

    <!-- User -->
    <div class="border-t border-slate-800 p-4">
        <div class="flex items-center gap-3">

            <div class="w-10 h-10 rounded-full bg-red-600 flex items-center justify-center font-bold text-white">
                {{ strtoupper(substr(auth()->user()->name,0,1)) }}
            </div>

            <div>
                <div class="font-semibold">
                    {{ auth()->user()->name }}
                </div>

                <div class="text-xs text-slate-400">
                    Administrator
                </div>
            </div>

        </div>
    </div>

</aside>