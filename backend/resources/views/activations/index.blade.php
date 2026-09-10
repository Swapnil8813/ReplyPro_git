@extends('layouts.app')

@section('title','Activations')
@section('page-title','Activation Management')

@section('content')

<div class="space-y-6">

    @if(session('success'))
        <div class="bg-green-100 border border-green-300 text-green-700 px-4 py-3 rounded-lg">
            {{ session('success') }}
        </div>
    @endif

    @if(session('error'))
        <div class="bg-red-100 border border-red-300 text-red-700 px-4 py-3 rounded-lg">
            {{ session('error') }}
        </div>
    @endif

    {{-- Header --}}
    <div class="bg-white rounded-xl shadow p-6 flex justify-between items-center">

        <div>

            <h1 class="text-2xl font-bold text-gray-800">
                Activation Management
            </h1>

            <p class="text-gray-500 mt-1">
                Manage all ReplyPro mobile activations.
            </p>

        </div>

        <a href="{{ route('activations.create') }}"
           class="bg-red-600 hover:bg-red-700 text-white px-6 py-3 rounded-lg shadow">

            + New Activation

        </a>

    </div>

    {{-- Search & Filters --}}
    <div class="bg-white rounded-xl shadow p-6">

        <form method="GET">

            <div class="grid grid-cols-1 md:grid-cols-5 gap-4">

                <input
                    type="text"
                    name="search"
                    value="{{ request('search') }}"
                    placeholder="Client / Mobile / Code"
                    class="border rounded-lg px-4 py-3 focus:ring-2 focus:ring-red-500">

                <select
                    name="status"
                    class="border rounded-lg px-4 py-3">

                    <option value="">All Status</option>

                    <option value="Active"
                        {{ request('status')=='Active'?'selected':'' }}>
                        Active
                    </option>

                    <option value="Expired"
                        {{ request('status')=='Expired'?'selected':'' }}>
                        Expired
                    </option>

                </select>

                <select
                    name="whatsapp"
                    class="border rounded-lg px-4 py-3">

                    <option value="">WhatsApp</option>

                    <option value="1"
                        {{ request('whatsapp')==='1'?'selected':'' }}>
                        Enabled
                    </option>

                    <option value="0"
                        {{ request('whatsapp')==='0'?'selected':'' }}>
                        Disabled
                    </option>

                </select>

                <select
                    name="sms"
                    class="border rounded-lg px-4 py-3">

                    <option value="">SMS</option>

                    <option value="1"
                        {{ request('sms')==='1'?'selected':'' }}>
                        Enabled
                    </option>

                    <option value="0"
                        {{ request('sms')==='0'?'selected':'' }}>
                        Disabled
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

                    <th class="px-4 py-4 text-left">
                        Client
                    </th>

                    <th class="px-4 py-4 text-left">
                        Mobile
                    </th>

                    <th class="px-4 py-4 text-center">
                        Start
                    </th>

                    <th class="px-4 py-4 text-center">
                        Expiry
                    </th>

                    <th class="px-4 py-4 text-center">
                        Days Left
                    </th>

                    <th class="px-4 py-4 text-center">
                        WhatsApp
                    </th>

                    <th class="px-4 py-4 text-center">
                        SMS
                    </th>

                    <th class="px-4 py-4 text-center">
                        Status
                    </th>

                    <th class="px-4 py-4 text-center">
                        Attachment
                    </th>

                    <th class="px-4 py-4 text-center">
                        Actions
                    </th>

                </tr>

                </thead>

                <tbody>
                    @forelse($activations as $activation)

@php

    $today = \Carbon\Carbon::today();

    $expiry = $activation->expiry_date;

    $daysLeft = $today->diffInDays($expiry, false);

@endphp

<tr class="border-b hover:bg-gray-50">

    {{-- Client --}}
    <td class="px-4 py-4">

        <div class="font-semibold">

            {{ $activation->client->name }}

        </div>

        <div class="text-xs text-gray-500">

            {{ $activation->client->client_code }}

        </div>

    </td>

    {{-- Mobile --}}
    <td class="px-4 py-4 font-medium">

        {{ $activation->mobile_number }}

    </td>

    {{-- Start --}}
    <td class="px-4 py-4 text-center">

        {{ $activation->start_date->format('d M Y') }}

    </td>

    {{-- Expiry --}}
    <td class="px-4 py-4 text-center">

        {{ $activation->expiry_date->format('d M Y') }}

    </td>

    {{-- Days Left --}}
    <td class="px-4 py-4 text-center">

        @if($daysLeft < 0)

            <span class="px-3 py-1 rounded-full bg-red-100 text-red-700 text-xs font-bold">

                Expired

            </span>

        @elseif($daysLeft == 0)

            <span class="px-3 py-1 rounded-full bg-orange-100 text-orange-700 text-xs font-bold">

                Today

            </span>

        @elseif($daysLeft <= 7)

            <span class="px-3 py-1 rounded-full bg-yellow-100 text-yellow-700 text-xs font-bold">

                {{ $daysLeft }} Days

            </span>

        @else

            <span class="px-3 py-1 rounded-full bg-green-100 text-green-700 text-xs font-bold">

                {{ $daysLeft }} Days

            </span>

        @endif

    </td>

    {{-- WhatsApp --}}
    <td class="px-4 py-4 text-center">

        @if($activation->whatsapp_enabled)

            <span class="text-green-600 text-xl">
                ✅
            </span>

        @else

            <span class="text-red-600 text-xl">
                ❌
            </span>

        @endif

    </td>

    {{-- SMS --}}
    <td class="px-4 py-4 text-center">

        @if($activation->sms_enabled)

            <span class="text-green-600 text-xl">
                ✅
            </span>

        @else

            <span class="text-red-600 text-xl">
                ❌
            </span>

        @endif

    </td>

    {{-- Status --}}
    <td class="px-4 py-4 text-center">

        @if($daysLeft < 0)

            <span class="px-3 py-1 rounded-full bg-red-100 text-red-700 text-xs font-semibold">

                Expired

            </span>

        @else

            <span class="px-3 py-1 rounded-full bg-green-100 text-green-700 text-xs font-semibold">

                Active

            </span>

        @endif

    </td>

    {{-- Attachment --}}
    <td class="px-4 py-4 text-center">

        @if($activation->whatsapp_attachment)

            @switch($activation->attachment_type)

                @case('image')

                    🖼️

                    @break

                @case('video')

                    🎥

                    @break

                @case('pdf')

                    📄

                    @break

            @endswitch

        @else

            —

        @endif

    </td>

    {{-- Actions --}}
    <td class="px-4 py-4">

        <div class="flex justify-center gap-2 flex-wrap">

            <a href="{{ route('activations.show',$activation) }}"
               class="bg-gray-700 hover:bg-gray-800 text-white px-3 py-2 rounded text-sm">

                View

            </a>

            <a href="{{ route('activations.edit',$activation) }}"
               class="bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-2 rounded text-sm">

                Edit

            </a>

            @if($activation->whatsapp_attachment)

                <a href="{{ asset('storage/'.$activation->whatsapp_attachment) }}"
                   target="_blank"
                   class="bg-blue-600 hover:bg-blue-700 text-white px-3 py-2 rounded text-sm">

                    File

                </a>

            @endif

            <form
                action="{{ route('activations.destroy',$activation) }}"
                method="POST">

                @csrf
                @method('DELETE')

                <button
                    onclick="return confirm('Delete this activation?')"
                    class="bg-red-600 hover:bg-red-700 text-white px-3 py-2 rounded text-sm">

                    Delete

                </button>

            </form>

        </div>

    </td>

</tr>

@empty

<tr>

    <td colspan="10" class="text-center py-10 text-gray-500">

        No activations found.

    </td>

</tr>

@endforelse

</tbody>

</table>

</div>

</div>

<div>

{{ $activations->links() }}

</div>

</div>

@endsection