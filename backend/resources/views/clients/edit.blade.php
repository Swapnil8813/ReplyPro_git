@extends('layouts.app')

@section('title', 'Edit Client')
@section('page-title', 'Edit Client')

@section('content')

<div class="max-w-7xl mx-auto">

    <div class="bg-white rounded-xl shadow">

        {{-- Header --}}
        <div class="border-b px-6 py-4 flex justify-between items-center">

            <div>

                <h2 class="text-2xl font-bold text-gray-800">
                    Edit Client
                </h2>

                <p class="text-gray-500">
                    Update client information.
                </p>

            </div>

            <a href="{{ route('clients.index') }}"
               class="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded-lg">

                ← Back

            </a>

        </div>

        <form action="{{ route('clients.update',$client) }}"
              method="POST">

            @csrf
            @method('PUT')

            <div class="p-6">

                @if ($errors->any())

                    <div class="mb-6 bg-red-100 border border-red-300 text-red-700 rounded-lg p-4">

                        <strong>Please fix the following errors:</strong>

                        <ul class="list-disc ml-6 mt-2">

                            @foreach ($errors->all() as $error)

                                <li>{{ $error }}</li>

                            @endforeach

                        </ul>

                    </div>

                @endif

                <div class="grid md:grid-cols-2 gap-6">

                    {{-- Client Code --}}
                    <div>

                        <label class="block mb-2 font-semibold">
                            Client Code
                        </label>

                        <input
                            type="text"
                            value="{{ $client->client_code }}"
                            class="w-full border rounded-lg px-4 py-3 bg-gray-100"
                            readonly>

                    </div>

                    {{-- Name --}}
                    <div>

                        <label class="block mb-2 font-semibold">
                            Client Name *
                        </label>

                        <input
                            type="text"
                            name="name"
                            value="{{ old('name',$client->name) }}"
                            class="w-full border rounded-lg px-4 py-3"
                            required>

                    </div>

                    {{-- Company --}}
                    <div>

                        <label class="block mb-2 font-semibold">
                            Company
                        </label>

                        <input
                            type="text"
                            name="company"
                            value="{{ old('company',$client->company) }}"
                            class="w-full border rounded-lg px-4 py-3">

                    </div>

                    {{-- Mobile --}}
                    <div>

                        <label class="block mb-2 font-semibold">
                            Mobile *
                        </label>

                        <input
                            type="text"
                            name="mobile"
                            value="{{ old('mobile',$client->mobile) }}"
                            class="w-full border rounded-lg px-4 py-3"
                            required>

                    </div>

                    {{-- Alternate Mobile --}}
                    <div>

                        <label class="block mb-2 font-semibold">
                            Alternate Mobile
                        </label>

                        <input
                            type="text"
                            name="alternate_mobile"
                            value="{{ old('alternate_mobile',$client->alternate_mobile) }}"
                            class="w-full border rounded-lg px-4 py-3">

                    </div>

                    {{-- Email --}}
                    <div>

                        <label class="block mb-2 font-semibold">
                            Email
                        </label>

                        <input
                            type="email"
                            name="email"
                            value="{{ old('email',$client->email) }}"
                            class="w-full border rounded-lg px-4 py-3">

                    </div>

                    {{-- GST --}}
                    <div>

                        <label class="block mb-2 font-semibold">
                            GST Number
                        </label>

                        <input
                            type="text"
                            name="gst_number"
                            value="{{ old('gst_number',$client->gst_number) }}"
                            class="w-full border rounded-lg px-4 py-3">

                    </div>

                    {{-- City --}}
                    <div>

                        <label class="block mb-2 font-semibold">
                            City
                        </label>

                        <input
                            type="text"
                            name="city"
                            value="{{ old('city',$client->city) }}"
                            class="w-full border rounded-lg px-4 py-3">

                    </div>

                    {{-- State --}}
                    <div>

                        <label class="block mb-2 font-semibold">
                            State
                        </label>

                        <input
                            type="text"
                            name="state"
                            value="{{ old('state',$client->state) }}"
                            class="w-full border rounded-lg px-4 py-3">

                    </div>

                    {{-- Plan --}}
                    <div>

                        <label class="block mb-2 font-semibold">
                            Plan
                        </label>

                        <select name="plan"
                                class="w-full border rounded-lg px-4 py-3">

                            <option value="Basic"
                                {{ old('plan',$client->plan)=='Basic'?'selected':'' }}>
                                Basic
                            </option>

                            <option value="Professional"
                                {{ old('plan',$client->plan)=='Professional'?'selected':'' }}>
                                Professional
                            </option>

                            <option value="Enterprise"
                                {{ old('plan',$client->plan)=='Enterprise'?'selected':'' }}>
                                Enterprise
                            </option>

                        </select>

                    </div>

                    {{-- Expiry --}}
                    <div>

                        <label class="block mb-2 font-semibold">
                            Expiry Date
                        </label>

                        <input
                            type="date"
                            name="expiry_date"
                            value="{{ old('expiry_date',$client->expiry_date) }}"
                            class="w-full border rounded-lg px-4 py-3">

                    </div>

                    {{-- Status --}}
                    <div>

                        <label class="block mb-2 font-semibold">
                            Status
                        </label>

                        <select name="status"
                                class="w-full border rounded-lg px-4 py-3">

                            <option value="Active"
                                {{ old('status',$client->status)=='Active'?'selected':'' }}>
                                Active
                            </option>

                            <option value="Inactive"
                                {{ old('status',$client->status)=='Inactive'?'selected':'' }}>
                                Inactive
                            </option>

                        </select>

                    </div>

                </div>

                {{-- Address --}}
                <div class="mt-6">

                    <label class="block mb-2 font-semibold">
                        Address
                    </label>

                    <textarea
                        name="address"
                        rows="3"
                        class="w-full border rounded-lg px-4 py-3">{{ old('address',$client->address) }}</textarea>

                </div>

                {{-- Notes --}}
                <div class="mt-6">

                    <label class="block mb-2 font-semibold">
                        Notes
                    </label>

                    <textarea
                        name="notes"
                        rows="4"
                        class="w-full border rounded-lg px-4 py-3">{{ old('notes',$client->notes) }}</textarea>

                </div>

            </div>

            {{-- Footer --}}
            <div class="border-t px-6 py-4 flex justify-end gap-3">

                <a href="{{ route('clients.index') }}"
                   class="bg-gray-500 hover:bg-gray-600 text-white px-6 py-3 rounded-lg">

                    Cancel

                </a>

                <button
                    type="submit"
                    class="bg-red-600 hover:bg-red-700 text-white px-8 py-3 rounded-lg font-semibold">

                    Update Client

                </button>

            </div>

        </form>

    </div>

</div>

@endsection