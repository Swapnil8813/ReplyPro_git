@extends('layouts.app')

@section('title', 'Clients')
@section('page-title', 'Clients Management')

@section('content')

<div class="space-y-6">

    {{-- Success Message --}}
    @if(session('success'))
        <div class="bg-green-100 border border-green-300 text-green-700 px-4 py-3 rounded-lg">
            {{ session('success') }}
        </div>
    @endif

    {{-- Error Message --}}
    @if(session('error'))
        <div class="bg-red-100 border border-red-300 text-red-700 px-4 py-3 rounded-lg">
            {{ session('error') }}
        </div>
    @endif

    {{-- Header --}}
    <div class="bg-white rounded-xl shadow p-6 flex justify-between items-center">

        <div>

            <h1 class="text-2xl font-bold text-gray-800">
                Clients
            </h1>

            <p class="text-gray-500 mt-1">
                Manage all ReplyPro clients
            </p>

        </div>

        <a href="{{ route('clients.create') }}"
           class="bg-red-600 hover:bg-red-700 text-white px-5 py-3 rounded-lg shadow">

            + Add Client

        </a>

    </div>

    {{-- Filters --}}
    <div class="bg-white rounded-xl shadow p-6">

        <form method="GET">

            <div class="grid grid-cols-1 md:grid-cols-4 gap-4">

                <input
                    type="text"
                    name="search"
                    value="{{ request('search') }}"
                    placeholder="Search by Code, Name, Mobile..."
                    class="border rounded-lg px-4 py-3 focus:ring-2 focus:ring-red-500 focus:outline-none">

                <select
                    name="plan"
                    class="border rounded-lg px-4 py-3 focus:ring-2 focus:ring-red-500">

                    <option value="">All Plans</option>

                    <option value="Basic"
                        {{ request('plan')=='Basic'?'selected':'' }}>
                        Basic
                    </option>

                    <option value="Professional"
                        {{ request('plan')=='Professional'?'selected':'' }}>
                        Professional
                    </option>

                    <option value="Enterprise"
                        {{ request('plan')=='Enterprise'?'selected':'' }}>
                        Enterprise
                    </option>

                </select>

                <select
                    name="status"
                    class="border rounded-lg px-4 py-3 focus:ring-2 focus:ring-red-500">

                    <option value="">All Status</option>

                    <option value="Active"
                        {{ request('status')=='Active'?'selected':'' }}>
                        Active
                    </option>

                    <option value="Inactive"
                        {{ request('status')=='Inactive'?'selected':'' }}>
                        Inactive
                    </option>

                </select>

                <button
                    class="bg-red-600 hover:bg-red-700 text-white rounded-lg">

                    Search

                </button>

            </div>

        </form>

    </div>

    {{-- Table --}}
    <div class="bg-white rounded-xl shadow overflow-hidden">

        <div class="overflow-x-auto">

            <table class="min-w-full">

                <thead class="bg-gray-100">

                <tr>

                    <th class="px-4 py-3 text-left">Code</th>

                    <th class="px-4 py-3 text-left">Client</th>

                    <th class="px-4 py-3 text-left">Mobile</th>

                    <th class="px-4 py-3 text-center">Plan</th>

                    <th class="px-4 py-3 text-center">Activations</th>

                    <th class="px-4 py-3 text-center">Status</th>

                    <th class="px-4 py-3 text-center">Actions</th>

                </tr>

                </thead>

                <tbody>

                @forelse($clients as $client)

                    <tr class="border-t hover:bg-gray-50">

                        <td class="px-4 py-4 font-semibold">
                            {{ $client->client_code }}
                        </td>

                        <td class="px-4 py-4">

                            <div class="font-semibold">

                                {{ $client->name }}

                            </div>

                            <div class="text-sm text-gray-500">

                                {{ $client->company ?: '-' }}

                            </div>

                        </td>

                        <td class="px-4 py-4">

                            {{ $client->mobile }}

                        </td>

                        <td class="px-4 py-4 text-center">

                            @if($client->plan=="Basic")

                                <span class="px-3 py-1 rounded-full bg-gray-200 text-gray-700 text-xs font-semibold">
                                    Basic
                                </span>

                            @elseif($client->plan=="Professional")

                                <span class="px-3 py-1 rounded-full bg-blue-100 text-blue-700 text-xs font-semibold">
                                    Professional
                                </span>

                            @else

                                <span class="px-3 py-1 rounded-full bg-purple-100 text-purple-700 text-xs font-semibold">
                                    Enterprise
                                </span>

                            @endif

                        </td>

                        <td class="px-4 py-4 text-center font-bold">

                            {{ $client->activations_count }}

                        </td>

                        <td class="px-4 py-4 text-center">

                            @if($client->status=="Active")

                                <span class="px-3 py-1 rounded-full bg-green-100 text-green-700 text-xs font-semibold">
                                    Active
                                </span>

                            @else

                                <span class="px-3 py-1 rounded-full bg-red-100 text-red-700 text-xs font-semibold">
                                    Inactive
                                </span>

                            @endif

                        </td>

                        <td class="px-4 py-4">

                            <div class="flex justify-center gap-2 flex-wrap">

                                <a href="{{ route('clients.show',$client) }}"
                                   class="bg-gray-700 hover:bg-gray-800 text-white px-3 py-1 rounded text-sm">

                                    View

                                </a>

                                <a href="{{ route('clients.edit',$client) }}"
                                   class="bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-1 rounded text-sm">

                                    Edit

                                </a>

                                <a href="https://wa.me/91{{ $client->mobile }}"
                                   target="_blank"
                                   class="bg-green-600 hover:bg-green-700 text-white px-3 py-1 rounded text-sm">

                                    WhatsApp

                                </a>

                                <a href="tel:{{ $client->mobile }}"
                                   class="bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded text-sm">

                                    Call

                                </a>

                                <form
                                    action="{{ route('clients.destroy',$client) }}"
                                    method="POST">

                                    @csrf
                                    @method('DELETE')

                                    <button
                                        onclick="return confirm('Delete this client?')"
                                        class="bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded text-sm">

                                        Delete

                                    </button>

                                </form>

                            </div>

                        </td>

                    </tr>

                @empty

                    <tr>

                        <td colspan="7" class="text-center py-10 text-gray-500">

                            No clients found.

                        </td>

                    </tr>

                @endforelse

                </tbody>

            </table>

        </div>

    </div>

    {{-- Pagination --}}
    <div>

        {{ $clients->links() }}

    </div>

</div>

@endsection