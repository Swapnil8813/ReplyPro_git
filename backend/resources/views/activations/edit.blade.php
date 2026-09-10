@extends('layouts.app')

@section('content')

<div class="max-w-5xl mx-auto p-6">

    <div class="flex justify-between items-center mb-6">
        <div>
            <h1 class="text-3xl font-bold">Edit Activation</h1>
            <p class="text-gray-500">Update activation details.</p>
        </div>

        <a href="{{ route('activations.index') }}"
           class="bg-gray-500 hover:bg-gray-600 text-white px-5 py-2 rounded-lg">
            Back
        </a>
    </div>

    {{-- Validation Errors --}}
    @if($errors->any())
        <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg mb-6">
            <ul class="list-disc ml-5">
                @foreach($errors->all() as $error)
                    <li>{{ $error }}</li>
                @endforeach
            </ul>
        </div>
    @endif

    <form action="{{ route('activations.update', $activation) }}"
          method="POST"
          enctype="multipart/form-data"
          class="bg-white rounded-xl shadow-lg p-8">

        @csrf
        @method('PUT')

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">

            <div>
                <label class="block font-semibold mb-2">Client</label>

                <select name="client_id"
                        class="w-full border rounded-lg p-3"
                        required>

                    @foreach($clients as $client)
                        <option value="{{ $client->id }}"
                            {{ $activation->client_id == $client->id ? 'selected' : '' }}>
                            {{ $client->name }}
                        </option>
                    @endforeach

                </select>
            </div>

            <div>
                <label class="block font-semibold mb-2">Mobile Number</label>

                <input
                    type="text"
                    name="mobile_number"
                    value="{{ old('mobile_number',$activation->mobile_number) }}"
                    class="w-full border rounded-lg p-3"
                    required>
            </div>

        </div>

        <hr class="my-8">

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">

            <div>
                <label class="block font-semibold mb-2">
                    Start Date
                </label>

                <input
                    type="date"
                    name="start_date"
                    value="{{ old('start_date', optional($activation->start_date)->format('Y-m-d')) }}"
                    class="w-full border rounded-lg p-3"
                    required>
            </div>

            <div>
                <label class="block font-semibold mb-2">
                    Expiry Date
                </label>

                <input
                    type="date"
                    name="expiry_date"
                    value="{{ old('expiry_date', optional($activation->expiry_date)->format('Y-m-d')) }}"
                    class="w-full border rounded-lg p-3"
                    required>
            </div>

        </div>

        <hr class="my-8">

        <h2 class="text-xl font-bold mb-4">
            WhatsApp Auto Reply
        </h2>

        <label class="flex items-center gap-2 mb-4">

            <input
                type="checkbox"
                name="whatsapp_enabled"
                value="1"
                {{ $activation->whatsapp_enabled ? 'checked' : '' }}>

            Enable WhatsApp Reply

        </label>

        <textarea
            name="whatsapp_message"
            rows="5"
            class="w-full border rounded-lg p-3"
            placeholder="Type WhatsApp message...">{{ old('whatsapp_message',$activation->whatsapp_message) }}</textarea>

        <div class="mt-5">

            <label class="block font-semibold mb-2">
                Change Attachment
            </label>

            <input
                type="file"
                name="whatsapp_attachment"
                id="attachment"
                class="w-full border rounded-lg p-3">

            @if($activation->whatsapp_attachment)

                <div class="mt-4 p-3 bg-gray-100 rounded">

                    <p class="font-semibold text-green-700">
                        Current Attachment
                    </p>

                    <a href="{{ asset('storage/'.$activation->whatsapp_attachment) }}"
                       target="_blank"
                       class="text-blue-600 underline break-all">

                        {{ basename($activation->whatsapp_attachment) }}

                    </a>

                </div>

            @endif

            <p id="selectedFile"
               class="text-green-600 mt-2"></p>

        </div>

        <hr class="my-8">

        <h2 class="text-xl font-bold mb-4">
            SMS Auto Reply
        </h2>

        <label class="flex items-center gap-2 mb-4">

            <input
                type="checkbox"
                name="sms_enabled"
                value="1"
                {{ $activation->sms_enabled ? 'checked' : '' }}>

            Enable SMS Reply

        </label>

        <textarea
            name="sms_message"
            rows="4"
            class="w-full border rounded-lg p-3">{{ old('sms_message',$activation->sms_message) }}</textarea>

        <hr class="my-8">

        <label class="block font-semibold mb-2">
            Remarks
        </label>

        <textarea
            name="remarks"
            rows="3"
            class="w-full border rounded-lg p-3">{{ old('remarks',$activation->remarks) }}</textarea>

        <div class="mt-8">

            <button
                class="bg-red-600 hover:bg-red-700 text-white px-6 py-3 rounded-lg shadow">

                Update Activation

            </button>

        </div>

    </form>

</div>

<script>

document.getElementById('attachment').addEventListener('change', function(){

    if(this.files.length){

        document.getElementById('selectedFile').innerHTML =
            "Selected : <strong>" + this.files[0].name + "</strong>";

    }

});

</script>

@endsection