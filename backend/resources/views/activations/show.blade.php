@extends('layouts.app')

@section('title', 'Activation Details')
@section('page-title', 'Activation Details')

@section('content')

<div class="max-w-7xl mx-auto space-y-6">

    {{-- Header --}}
    <div class="bg-white rounded-xl shadow p-6 flex flex-col md:flex-row md:justify-between md:items-center">

        <div>

            <h1 class="text-2xl font-bold text-gray-800">
                Activation Details
            </h1>

            <p class="text-gray-500 mt-1">
                Complete activation and device information.
            </p>

        </div>

        <div class="flex gap-3 mt-4 md:mt-0">

            <a href="{{ route('activations.edit',$activation) }}"
               class="bg-yellow-500 hover:bg-yellow-600 text-white px-5 py-3 rounded-lg">

                Edit Activation

            </a>

            <a href="{{ route('activations.index') }}"
               class="bg-gray-700 hover:bg-gray-800 text-white px-5 py-3 rounded-lg">

                Back

            </a>

        </div>

    </div>

    {{-- Activation Information --}}
    <div class="bg-white rounded-xl shadow">

        <div class="border-b px-6 py-4">

            <h2 class="text-xl font-bold text-gray-800">
                Activation Information
            </h2>

        </div>

        <div class="p-6">

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

                <div>

                    <label class="text-sm text-gray-500">
                        Client
                    </label>

                    <div class="font-semibold mt-1">
                        {{ $activation->client->name }}
                    </div>

                </div>

                <div>

                    <label class="text-sm text-gray-500">
                        Client Code
                    </label>

                    <div class="font-semibold mt-1">
                        {{ $activation->client->client_code }}
                    </div>

                </div>

                <div>

                    <label class="text-sm text-gray-500">
                        Mobile Number
                    </label>

                    <div class="font-semibold mt-1">
                        {{ $activation->mobile_number }}
                    </div>

                </div>

                <div>

                    <label class="text-sm text-gray-500">
                        Start Date
                    </label>

                    <div class="font-semibold mt-1">
                        {{ \Carbon\Carbon::parse($activation->start_date)->format('d M Y') }}
                    </div>

                </div>

                <div>

                    <label class="text-sm text-gray-500">
                        Expiry Date
                    </label>

                    <div class="font-semibold mt-1">
                        {{ \Carbon\Carbon::parse($activation->expiry_date)->format('d M Y') }}
                    </div>

                </div>

                <div>

                    <label class="text-sm text-gray-500">
                        License Status
                    </label>

                    <div class="mt-2">

                        @if($activation->status=='Active')

                            <span class="bg-green-100 text-green-700 px-3 py-1 rounded-full text-sm font-semibold">
                                Active
                            </span>

                        @elseif($activation->status=='Expired')

                            <span class="bg-red-100 text-red-700 px-3 py-1 rounded-full text-sm font-semibold">
                                Expired
                            </span>

                        @else

                            <span class="bg-gray-200 text-gray-700 px-3 py-1 rounded-full text-sm font-semibold">
                                {{ $activation->status }}
                            </span>

                        @endif

                    </div>

                </div>

            </div>

        </div>

    </div>

    {{-- WhatsApp Configuration --}}
    <div class="bg-white rounded-xl shadow">

        <div class="border-b px-6 py-4">

            <h2 class="text-xl font-bold text-gray-800">
                WhatsApp Configuration
            </h2>

        </div>

        <div class="p-6 space-y-5">

            <div>

                <label class="text-sm text-gray-500">
                    WhatsApp Status
                </label>

                <div class="mt-2">

                    @if($activation->whatsapp_enabled)

                        <span class="bg-green-100 text-green-700 px-3 py-1 rounded-full text-sm font-semibold">
                            Enabled
                        </span>

                    @else

                        <span class="bg-red-100 text-red-700 px-3 py-1 rounded-full text-sm font-semibold">
                            Disabled
                        </span>

                    @endif

                </div>

            </div>

            <div>

                <label class="text-sm text-gray-500">
                    Auto Reply Message
                </label>

                <div class="bg-gray-50 rounded-lg border p-4 mt-2 whitespace-pre-wrap">

                    {{ $activation->whatsapp_message ?: 'No message configured.' }}

                </div>

            </div>

            <div>

                <label class="text-sm text-gray-500">
                    Attachment
                </label>

                <div class="mt-2">

                    @if($activation->whatsapp_attachment)

                        <a href="{{ $activation->whatsapp_attachment }}"
   target="_blank"
   class="text-red-600 font-semibold hover:underline">

                            View Attachment

                        </a>

                    @else

                        <span class="text-gray-500">
                            No Attachment
                        </span>

                    @endif

                </div>

            </div>

        </div>

    </div>

    {{-- SMS Configuration --}}
    <div class="bg-white rounded-xl shadow">

        <div class="border-b px-6 py-4">

            <h2 class="text-xl font-bold text-gray-800">
                SMS Configuration
            </h2>

        </div>

        <div class="p-6 space-y-5">

            <div>

                @if($activation->sms_enabled)

                    <span class="bg-green-100 text-green-700 px-3 py-1 rounded-full text-sm font-semibold">
                        Enabled
                    </span>

                @else

                    <span class="bg-red-100 text-red-700 px-3 py-1 rounded-full text-sm font-semibold">
                        Disabled
                    </span>

                @endif

            </div>

            <div class="bg-gray-50 rounded-lg border p-4 whitespace-pre-wrap">

                {{ $activation->sms_message ?: 'No SMS message configured.' }}

            </div>

        </div>

    </div>
        {{-- Device Information --}}
    <div class="bg-white rounded-xl shadow">

        <div class="border-b px-6 py-4">

            <h2 class="text-xl font-bold text-gray-800">
                📱 Device Information
            </h2>

        </div>

        <div class="p-6">

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">

                <div>

                    <label class="text-sm text-gray-500">
                        Device ID
                    </label>

                    <div class="font-semibold break-all mt-1">
                        {{ $activation->device_id ?: 'Not Registered' }}
                    </div>

                </div>

                <div>

                    <label class="text-sm text-gray-500">
                        Device Name
                    </label>

                    <div class="font-semibold mt-1">
                        {{ $activation->device_name ?: 'Not Registered' }}
                    </div>

                </div>

                <div>

                    <label class="text-sm text-gray-500">
                        App Version
                    </label>

                    <div class="font-semibold mt-1">
                        {{ $activation->app_version ?: '-' }}
                    </div>

                </div>

                <div>

                    <label class="text-sm text-gray-500">
                        Last Seen
                    </label>

                    <div class="font-semibold mt-1">
                        {{ optional($activation->last_seen)->format('d M Y h:i A') ?? 'Never' }}
                    </div>

                </div>

                <div>

                    <label class="text-sm text-gray-500">
                        Device Status
                    </label>

                    <div class="mt-2">

                        @if($activation->blocked)

                            <span class="bg-red-100 text-red-700 px-3 py-1 rounded-full text-sm font-semibold">
                                🚫 Blocked
                            </span>

                        @elseif($activation->device_id)

                            <span class="bg-green-100 text-green-700 px-3 py-1 rounded-full text-sm font-semibold">
                                ✅ Active
                            </span>

                        @else

                            <span class="bg-yellow-100 text-yellow-700 px-3 py-1 rounded-full text-sm font-semibold">
                                ⏳ Waiting for First Login
                            </span>

                        @endif

                    </div>

                </div>

            </div>

        </div>

    </div>

    {{-- Device Controls --}}
    <div class="bg-white rounded-xl shadow">

        <div class="border-b px-6 py-4">

            <h2 class="text-xl font-bold text-gray-800">
                Device Controls
            </h2>

        </div>

        <div class="p-6 flex flex-wrap gap-4">

            <form action="{{ route('activations.resetDevice',$activation) }}" method="POST">

                @csrf

                <button
                    onclick="return confirm('Reset this registered device?')"
                    class="bg-yellow-500 hover:bg-yellow-600 text-white px-5 py-3 rounded-lg">

                    🔄 Reset Device

                </button>

            </form>

            @if(!$activation->blocked)

                <form action="{{ route('activations.blockDevice',$activation) }}" method="POST">

                    @csrf

                    <button
                        onclick="return confirm('Block this activation?')"
                        class="bg-red-600 hover:bg-red-700 text-white px-5 py-3 rounded-lg">

                        🚫 Block Device

                    </button>

                </form>

            @else

                <form action="{{ route('activations.unblockDevice',$activation) }}" method="POST">

                    @csrf

                    <button
                        class="bg-green-600 hover:bg-green-700 text-white px-5 py-3 rounded-lg">

                        ✅ Unblock Device

                    </button>

                </form>

            @endif

        </div>

    </div>

    {{-- Remarks --}}
    <div class="bg-white rounded-xl shadow">

        <div class="border-b px-6 py-4">

            <h2 class="text-xl font-bold text-gray-800">
                Remarks
            </h2>

        </div>

        <div class="p-6">

            <div class="bg-gray-50 rounded-lg border p-4 whitespace-pre-wrap">

                {{ $activation->remarks ?: 'No remarks available.' }}

            </div>

        </div>

    </div>

    {{-- Footer --}}
    <div class="text-center text-gray-500 text-sm py-4">

        ReplyPro License Management System

    </div>

</div>

@endsection