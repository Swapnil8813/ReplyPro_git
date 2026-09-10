<header class="bg-white border-b border-gray-200 h-16 flex items-center justify-between px-6 shadow-sm">

    <!-- Left -->
    <div>
        <h2 class="text-2xl font-bold text-gray-800">
            @yield('page-title', 'Dashboard')
        </h2>
    </div>

    <!-- Right -->
    <div class="flex items-center gap-5">

        <!-- Search -->
        <div class="hidden md:block">
            <input
                type="text"
                placeholder="Search..."
                class="w-72 rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>

        <!-- Notification -->
        <button
            class="relative w-10 h-10 rounded-full hover:bg-gray-100 flex items-center justify-center transition">

            🔔

            <span
                class="absolute top-2 right-2 w-2 h-2 bg-red-500 rounded-full"></span>

        </button>

        <!-- User -->
        <div class="flex items-center gap-3">

            <div
                class="w-10 h-10 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">

                {{ strtoupper(substr(auth()->user()->name,0,1)) }}

            </div>

            <div class="hidden md:block">

                <div class="font-semibold">

                    {{ auth()->user()->name }}

                </div>

                <div class="text-xs text-gray-500">

                    Administrator

                </div>

            </div>

        </div>

        <!-- Logout -->
        <form action="{{ route('logout') }}" method="POST">

            @csrf

            <button
                class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg transition">

                Logout

            </button>

        </form>

    </div>

</header>