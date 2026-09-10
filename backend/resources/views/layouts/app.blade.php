<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('title', 'ReplyPro')</title>

    @vite(['resources/css/app.css', 'resources/js/app.js'])
</head>

<body class="bg-slate-100">

<div class="min-h-screen flex">

    {{-- Sidebar --}}
    @include('layouts.sidebar')

    {{-- Main Content --}}
    <div class="flex-1 flex flex-col">

        {{-- Navbar --}}
        @include('layouts.navbar')

        {{-- Page Content --}}
        <main class="flex-1 p-6 overflow-y-auto">

            @if(session('success'))
                <div class="mb-4 rounded-lg border border-green-300 bg-green-100 px-4 py-3 text-green-700">
                    {{ session('success') }}
                </div>
            @endif

            @yield('content')

        </main>

        {{-- Footer --}}
        @include('layouts.footer')

    </div>

</div>

</body>
</html>