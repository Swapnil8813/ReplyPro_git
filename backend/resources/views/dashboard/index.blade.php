@extends('layouts.app')

@section('content')

<div class="max-w-7xl mx-auto px-6 py-8">

    <div class="flex items-center justify-between mb-8">
        <div>
            <h1 class="text-3xl font-bold text-gray-800">
                ReplyPro Dashboard
            </h1>

            <p class="text-gray-500 mt-1">
                Welcome back! Here's your activation overview.
            </p>
        </div>

        <div class="flex gap-3">

            <a href="{{ route('clients.create') }}"
               class="bg-red-600 hover:bg-red-700 text-white px-5 py-2 rounded-lg shadow">
                + Client
            </a>

            <a href="{{ route('activations.create') }}"
               class="bg-green-600 hover:bg-green-700 text-white px-5 py-2 rounded-lg shadow">
                + Activation
            </a>

        </div>

    </div>


    {{-- Statistics --}}

    <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">

        <div class="bg-white rounded-xl shadow p-6">

            <div class="text-gray-500 text-sm">
                Total Clients
            </div>

            <div class="text-4xl font-bold text-red-600 mt-2">
                {{ $totalClients }}
            </div>

        </div>


        <div class="bg-white rounded-xl shadow p-6">

            <div class="text-gray-500 text-sm">
                Total Activations
            </div>

            <div class="text-4xl font-bold text-blue-600 mt-2">
                {{ $totalActivations }}
            </div>

        </div>


        <div class="bg-white rounded-xl shadow p-6">

            <div class="text-gray-500 text-sm">
                Active Licenses
            </div>

            <div class="text-4xl font-bold text-green-600 mt-2">
                {{ $activeLicenses }}
            </div>

        </div>


        <div class="bg-white rounded-xl shadow p-6">

            <div class="text-gray-500 text-sm">
                Expired Licenses
            </div>

            <div class="text-4xl font-bold text-red-700 mt-2">
                {{ $expiredLicenses }}
            </div>

        </div>


        <div class="bg-white rounded-xl shadow p-6">

            <div class="text-gray-500 text-sm">
                Expiring Within 7 Days
            </div>

            <div class="text-4xl font-bold text-yellow-500 mt-2">
                {{ $expiringSoon }}
            </div>

        </div>


        <div class="bg-white rounded-xl shadow p-6">

            <div class="text-gray-500 text-sm">
                This Month Activations
            </div>

            <div class="text-4xl font-bold text-indigo-600 mt-2">
                {{ $monthlyActivations }}
            </div>

        </div>

    </div>
        {{-- Latest Activations --}}

    <div class="bg-white rounded-xl shadow mt-8">

        <div class="flex items-center justify-between border-b px-6 py-4">

            <h2 class="text-xl font-bold text-gray-800">
                Latest Activations
            </h2>

            <a href="{{ route('activations.index') }}"
               class="text-red-600 hover:text-red-700 font-semibold">
                View All →
            </a>

        </div>

        <div class="overflow-x-auto">

            <table class="min-w-full">

                <thead class="bg-gray-100">

                    <tr>

                        <th class="px-6 py-3 text-left text-sm font-semibold">
                            Client
                        </th>

                        <th class="px-6 py-3 text-left text-sm font-semibold">
                            Mobile
                        </th>

                        <th class="px-6 py-3 text-left text-sm font-semibold">
                            Expiry Date
                        </th>

                        <th class="px-6 py-3 text-center text-sm font-semibold">
                            Status
                        </th>

                        <th class="px-6 py-3 text-center text-sm font-semibold">
                            Action
                        </th>

                    </tr>

                </thead>

                <tbody>

                @forelse($latestActivations as $activation)

                    <tr class="border-b hover:bg-gray-50">

                        <td class="px-6 py-4">

                            {{ $activation->client->name }}

                        </td>

                        <td class="px-6 py-4">

                            {{ $activation->mobile_number }}

                        </td>

                        <td class="px-6 py-4">

                            {{ \Carbon\Carbon::parse($activation->expiry_date)->format('d M Y') }}

                        </td>

                        <td class="px-6 py-4 text-center">

                            @if($activation->expiry_date < now()->toDateString())

                                <span class="bg-red-100 text-red-700 px-3 py-1 rounded-full text-xs font-semibold">
                                    Expired
                                </span>

                            @elseif($activation->expiry_date <= now()->addDays(7)->toDateString())

                                <span class="bg-yellow-100 text-yellow-700 px-3 py-1 rounded-full text-xs font-semibold">
                                    Expiring Soon
                                </span>

                            @else

                                <span class="bg-green-100 text-green-700 px-3 py-1 rounded-full text-xs font-semibold">
                                    Active
                                </span>

                            @endif

                        </td>

                        <td class="px-6 py-4 text-center">

                            <a href="{{ route('activations.edit',$activation) }}"
                               class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg text-sm">

                                Edit

                            </a>

                        </td>

                    </tr>

                @empty

                    <tr>

                        <td colspan="5" class="text-center py-10 text-gray-500">

                            No activations found.

                        </td>

                    </tr>

                @endforelse

                </tbody>

            </table>

        </div>

    </div>
        {{-- Bottom Section --}}

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-8">

        {{-- Quick Actions --}}
        <div class="bg-white rounded-xl shadow p-6">

            <h2 class="text-xl font-bold text-gray-800 mb-5">
                Quick Actions
            </h2>

            <div class="grid grid-cols-2 gap-4">

                <a href="{{ route('clients.create') }}"
                    class="bg-red-600 hover:bg-red-700 text-white rounded-lg p-5 text-center transition">

                    <div class="text-3xl mb-2">👤</div>

                    <div class="font-semibold">
                        Add Client
                    </div>

                </a>

                <a href="{{ route('activations.create') }}"
                    class="bg-green-600 hover:bg-green-700 text-white rounded-lg p-5 text-center transition">

                    <div class="text-3xl mb-2">📱</div>

                    <div class="font-semibold">
                        New Activation
                    </div>

                </a>

                <a href="{{ route('clients.index') }}"
                    class="bg-blue-600 hover:bg-blue-700 text-white rounded-lg p-5 text-center transition">

                    <div class="text-3xl mb-2">📋</div>

                    <div class="font-semibold">
                        Clients
                    </div>

                </a>

                <a href="{{ route('activations.index') }}"
                    class="bg-purple-600 hover:bg-purple-700 text-white rounded-lg p-5 text-center transition">

                    <div class="text-3xl mb-2">📊</div>

                    <div class="font-semibold">
                        Activations
                    </div>

                </a>

            </div>

        </div>

        {{-- System Status --}}
        <div class="bg-white rounded-xl shadow p-6">

            <h2 class="text-xl font-bold text-gray-800 mb-5">
                System Status
            </h2>

            <div class="space-y-4">

                <div class="flex justify-between items-center">
                    <span>Laravel</span>
                    <span class="bg-green-100 text-green-700 px-3 py-1 rounded-full text-sm">
                        Running
                    </span>
                </div>

                <div class="flex justify-between items-center">
                    <span>Database</span>
                    <span class="bg-green-100 text-green-700 px-3 py-1 rounded-full text-sm">
                        Connected
                    </span>
                </div>

                <div class="flex justify-between items-center">
                    <span>Total Records</span>
                    <span class="font-bold">
                        {{ $totalClients + $totalActivations }}
                    </span>
                </div>

                <div class="flex justify-between items-center">
                    <span>ReplyPro Version</span>
                    <span class="font-bold text-red-600">
                        V1.0
                    </span>
                </div>

            </div>

        </div>

    </div>

    {{-- Footer --}}

    <div class="mt-10 text-center text-gray-500 text-sm">

        © {{ date('Y') }} ReplyPro | Developed by Digitechno

    </div>

</div>

@endsection