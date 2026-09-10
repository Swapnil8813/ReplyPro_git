@extends('layouts.app')

@section('title','Client Details')
@section('page-title','Client Details')

@section('content')

<div class="space-y-6">

    {{-- Header --}}
    <div class="bg-white rounded-xl shadow p-6 flex justify-between items-center">

        <div>

            <h2 class="text-2xl font-bold text-gray-800">
                {{ $client->name }}
            </h2>

            <p class="text-gray-500">
                Client Code : {{ $client->client_code }}
            </p>

        </div>

        <div class="flex gap-3">

            <a href="{{ route('clients.edit',$client) }}"
               class="bg-yellow-500 hover:bg-yellow-600 text-white px-5 py-2 rounded-lg">

                Edit Client

            </a>

            <a href="{{ route('clients.index') }}"
               class="bg-gray-600 hover:bg-gray-700 text-white px-5 py-2 rounded-lg">

                Back

            </a>

        </div>

    </div>

    {{-- Summary Cards --}}
    <div class="grid md:grid-cols-4 gap-6">

        <div class="bg-white rounded-xl shadow p-5">

            <p class="text-gray-500 text-sm">
                Plan
            </p>

            <h3 class="text-xl font-bold mt-2">
                {{ $client->plan }}
            </h3>

        </div>

        <div class="bg-white rounded-xl shadow p-5">

            <p class="text-gray-500 text-sm">
                Status
            </p>

            @if($client->status=="Active")

                <span class="inline-block mt-3 px-4 py-2 rounded-full bg-green-100 text-green-700 font-semibold">

                    Active

                </span>

            @else

                <span class="inline-block mt-3 px-4 py-2 rounded-full bg-red-100 text-red-700 font-semibold">

                    Inactive

                </span>

            @endif

        </div>

        <div class="bg-white rounded-xl shadow p-5">

            <p class="text-gray-500 text-sm">
                Expiry Date
            </p>

            <h3 class="text-xl font-bold mt-2">

                {{ $client->expiry_date ?? '-' }}

            </h3>

        </div>

        <div class="bg-white rounded-xl shadow p-5">

            <p class="text-gray-500 text-sm">
                Total Activations
            </p>

            <h3 class="text-3xl font-bold text-red-600 mt-2">

                {{ $client->activations_count }}

            </h3>

        </div>

    </div>

    {{-- Client Information --}}
    <div class="bg-white rounded-xl shadow">

        <div class="border-b px-6 py-4">

            <h3 class="text-xl font-bold">

                Client Information

            </h3>

        </div>

        <div class="grid md:grid-cols-2 gap-6 p-6">

            <div>

                <p class="text-gray-500">

                    Company

                </p>

                <h4 class="font-semibold">

                    {{ $client->company ?: '-' }}

                </h4>

            </div>

            <div>

                <p class="text-gray-500">

                    Mobile

                </p>

                <h4 class="font-semibold">

                    {{ $client->mobile }}

                </h4>

            </div>

            <div>

                <p class="text-gray-500">

                    Alternate Mobile

                </p>

                <h4 class="font-semibold">

                    {{ $client->alternate_mobile ?: '-' }}

                </h4>

            </div>

            <div>

                <p class="text-gray-500">

                    Email

                </p>

                <h4 class="font-semibold">

                    {{ $client->email ?: '-' }}

                </h4>

            </div>

            <div>

                <p class="text-gray-500">

                    GST Number

                </p>

                <h4 class="font-semibold">

                    {{ $client->gst_number ?: '-' }}

                </h4>

            </div>

            <div>

                <p class="text-gray-500">

                    City

                </p>

                <h4 class="font-semibold">

                    {{ $client->city ?: '-' }}

                </h4>

            </div>

            <div>

                <p class="text-gray-500">

                    State

                </p>

                <h4 class="font-semibold">

                    {{ $client->state ?: '-' }}

                </h4>

            </div>

            <div>

                <p class="text-gray-500">

                    Address

                </p>

                <h4 class="font-semibold">

                    {{ $client->address ?: '-' }}

                </h4>

            </div>

        </div>

    </div>

    {{-- Notes --}}
    <div class="bg-white rounded-xl shadow">

        <div class="border-b px-6 py-4">

            <h3 class="text-xl font-bold">

                Notes

            </h3>

        </div>

        <div class="p-6">

            {{ $client->notes ?: 'No Notes Available.' }}

        </div>

    </div>

    {{-- Latest Activations --}}
    <div class="bg-white rounded-xl shadow">

        <div class="border-b px-6 py-4 flex justify-between items-center">

            <h3 class="text-xl font-bold">

                Latest Activations

            </h3>

            <span class="text-sm text-gray-500">

                Last 10 Records

            </span>

        </div>

        <div class="overflow-x-auto">

            <table class="min-w-full">

                <thead class="bg-gray-100">

                    <tr>

                        <th class="px-4 py-3 text-left">
                            Mobile
                        </th>

                        <th class="px-4 py-3 text-left">
                            Start Date
                        </th>

                        <th class="px-4 py-3 text-left">
                            Expiry
                        </th>

                        <th class="px-4 py-3 text-center">
                            Status
                        </th>

                    </tr>

                </thead>

                <tbody>

                @forelse($latestActivations as $activation)

                    <tr class="border-t">

                        <td class="px-4 py-3">

                            {{ $activation->mobile_number }}

                        </td>

                        <td class="px-4 py-3">

                            {{ $activation->start_date }}

                        </td>

                        <td class="px-4 py-3">

                            {{ $activation->expiry_date }}

                        </td>

                        <td class="px-4 py-3 text-center">

                            @if($activation->status=="Active")

                                <span class="px-3 py-1 rounded-full bg-green-100 text-green-700 text-xs">

                                    Active

                                </span>

                            @else

                                <span class="px-3 py-1 rounded-full bg-red-100 text-red-700 text-xs">

                                    Expired

                                </span>

                            @endif

                        </td>

                    </tr>

                @empty

                    <tr>

                        <td colspan="4" class="text-center py-8 text-gray-500">

                            No activations found.

                        </td>

                    </tr>

                @endforelse

                </tbody>

            </table>

        </div>

    </div>

    {{-- Quick Actions --}}
    <div class="bg-white rounded-xl shadow p-6">

        <h3 class="text-xl font-bold mb-5">

            Quick Actions

        </h3>

        <div class="flex flex-wrap gap-4">

            <a href="https://wa.me/91{{ $client->mobile }}"
               target="_blank"
               class="bg-green-600 hover:bg-green-700 text-white px-5 py-3 rounded-lg">

                WhatsApp

            </a>

            <a href="tel:{{ $client->mobile }}"
               class="bg-blue-600 hover:bg-blue-700 text-white px-5 py-3 rounded-lg">

                Call Client

            </a>

            <a href="{{ route('activations.create') }}?client={{ $client->id }}"
               class="bg-red-600 hover:bg-red-700 text-white px-5 py-3 rounded-lg">

                New Activation

            </a>

        </div>

    </div>

</div>

@endsection