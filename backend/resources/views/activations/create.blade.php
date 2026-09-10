@extends('layouts.app')

@section('title','New Activation')
@section('page-title','Create Activation')

@section('content')

<div class="max-w-7xl mx-auto">

    <div class="bg-white rounded-xl shadow">

        {{-- Header --}}
        <div class="border-b px-6 py-4 flex justify-between items-center">

            <div>

                <h2 class="text-2xl font-bold text-gray-800">
                    New Activation
                </h2>

                <p class="text-gray-500">
                    Activate a mobile number for ReplyPro.
                </p>

            </div>

            <a href="{{ route('activations.index') }}"
               class="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded-lg">

                ← Back

            </a>

        </div>

        <form action="{{ route('activations.store') }}"
              method="POST"
              enctype="multipart/form-data">

            @csrf

            <div class="p-6">

                @if ($errors->any())

                    <div class="mb-6 bg-red-100 border border-red-300 text-red-700 rounded-lg p-4">

                        <strong>Please fix the following errors:</strong>

                        <ul class="list-disc ml-6 mt-2">

                            @foreach($errors->all() as $error)

                                <li>{{ $error }}</li>

                            @endforeach

                        </ul>

                    </div>

                @endif

                <div class="grid md:grid-cols-2 gap-6">

                    {{-- Client --}}
                    <div>

                        <label class="block mb-2 font-semibold">

                            Client
                            <span class="text-red-600">*</span>

                        </label>

                        <select
                            name="client_id"
                            class="w-full border rounded-lg px-4 py-3"
                            required>

                            <option value="">

                                Select Client

                            </option>

                            @foreach($clients as $client)

                                <option
                                    value="{{ $client->id }}"
                                    {{ old('client_id', request('client')) == $client->id ? 'selected' : '' }}>

                                    {{ $client->client_code }}
                                    -
                                    {{ $client->name }}

                                </option>

                            @endforeach

                        </select>

                    </div>

                    {{-- Mobile --}}
                    <div>

                        <label class="block mb-2 font-semibold">

                            Mobile Number
                            <span class="text-red-600">*</span>

                        </label>

                        <input
                            type="text"
                            name="mobile_number"
                            value="{{ old('mobile_number') }}"
                            maxlength="10"
                            class="w-full border rounded-lg px-4 py-3"
                            placeholder="9876543210"
                            required>

                    </div>

                    {{-- Start Date --}}
                    <div>

                        <label class="block mb-2 font-semibold">

                            Start Date

                        </label>

                        <input
                            id="start_date"
                            type="date"
                            name="start_date"
                            value="{{ old('start_date', now()->format('Y-m-d')) }}"
                            class="w-full border rounded-lg px-4 py-3">

                    </div>

                    {{-- Expiry Date --}}
                    <div>

                        <label class="block mb-2 font-semibold">

                            Expiry Date

                        </label>

                        <input
                            id="expiry_date"
                            type="date"
                            name="expiry_date"
                            value="{{ old('expiry_date', now()->addYear()->format('Y-m-d')) }}"
                            class="w-full border rounded-lg px-4 py-3">

                    </div>

                </div>

                {{-- Duration Presets --}}
                <div class="mt-8">

                    <label class="block mb-3 font-semibold">

                        Quick Duration

                    </label>

                    <div class="flex flex-wrap gap-3">

                        <button
                            type="button"
                            class="duration-btn bg-gray-200 hover:bg-red-600 hover:text-white px-5 py-2 rounded-lg"
                            data-days="30">

                            30 Days

                        </button>

                        <button
                            type="button"
                            class="duration-btn bg-gray-200 hover:bg-red-600 hover:text-white px-5 py-2 rounded-lg"
                            data-days="90">

                            90 Days

                        </button>

                        <button
                            type="button"
                            class="duration-btn bg-gray-200 hover:bg-red-600 hover:text-white px-5 py-2 rounded-lg"
                            data-days="180">

                            180 Days

                        </button>

                        <button
                            type="button"
                            class="duration-btn bg-red-600 text-white px-5 py-2 rounded-lg"
                            data-days="365">

                            365 Days

                        </button>

                    </div>

                </div>
                                {{-- WhatsApp Settings --}}
                <div class="mt-8">

                    <div class="bg-green-50 border border-green-200 rounded-xl p-6">

                        <h3 class="text-xl font-bold text-green-700 mb-5">

                            WhatsApp Configuration

                        </h3>

                        <div class="flex items-center gap-3 mb-6">

                            <input
                                type="checkbox"
                                id="whatsapp_enabled"
                                name="whatsapp_enabled"
                                value="1"
                                {{ old('whatsapp_enabled', true) ? 'checked' : '' }}
                                class="w-5 h-5">

                            <label
                                for="whatsapp_enabled"
                                class="font-semibold">

                                Enable WhatsApp Auto Reply

                            </label>

                        </div>

                        <div>

                            <label class="block mb-2 font-semibold">

                                WhatsApp Message

                            </label>

                            <textarea
                                id="whatsapp_message"
                                name="whatsapp_message"
                                rows="6"
                                class="w-full border rounded-lg px-4 py-3"
                                placeholder="Enter WhatsApp auto reply...">{{ old('whatsapp_message') }}</textarea>

                            <div class="text-right text-sm text-gray-500 mt-2">

                                Characters :
                                <span id="waCount">

                                    0

                                </span>

                            </div>

                        </div>

                        <div class="mt-6">

                            <label class="block mb-2 font-semibold">

                                Attachment

                            </label>

                            <input
                                type="file"
                                id="attachment"
                                name="whatsapp_attachment"
                                accept=".jpg,.jpeg,.png,.pdf,.mp4,.mov,.avi"
                                class="w-full border rounded-lg px-4 py-3">

                            <p class="text-sm text-gray-500 mt-2">

                                Supported:
                                JPG, PNG, PDF, MP4, MOV, AVI
                                (Max 50MB)

                            </p>

                            <div
                                id="filePreview"
                                class="mt-4 hidden bg-gray-100 rounded-lg p-3">

                                <strong>

                                    Selected File:

                                </strong>

                                <span id="fileName"></span>

                            </div>

                        </div>

                    </div>

                </div>

                {{-- SMS Settings --}}
                <div class="mt-8">

                    <div class="bg-blue-50 border border-blue-200 rounded-xl p-6">

                        <h3 class="text-xl font-bold text-blue-700 mb-5">

                            SMS Configuration

                        </h3>

                        <div class="flex items-center gap-3 mb-6">

                            <input
                                type="checkbox"
                                id="sms_enabled"
                                name="sms_enabled"
                                value="1"
                                {{ old('sms_enabled', true) ? 'checked' : '' }}
                                class="w-5 h-5">

                            <label
                                for="sms_enabled"
                                class="font-semibold">

                                Enable SMS Auto Reply

                            </label>

                        </div>

                        <textarea
                            id="sms_message"
                            name="sms_message"
                            rows="4"
                            class="w-full border rounded-lg px-4 py-3"
                            placeholder="Enter SMS auto reply...">{{ old('sms_message') }}</textarea>

                        <div class="text-right text-sm text-gray-500 mt-2">

                            Characters :
                            <span id="smsCount">

                                0

                            </span>

                        </div>

                    </div>

                </div>

                {{-- Remarks --}}
                <div class="mt-8">

                    <label class="block mb-2 font-semibold">

                        Remarks

                    </label>

                    <textarea
                        name="remarks"
                        rows="4"
                        class="w-full border rounded-lg px-4 py-3"
                        placeholder="Internal notes...">{{ old('remarks') }}</textarea>

                </div>

            </div>

            {{-- Footer --}}
            <div class="border-t px-6 py-4 flex justify-end gap-3">

                <a href="{{ route('activations.index') }}"
                   class="bg-gray-600 hover:bg-gray-700 text-white px-6 py-3 rounded-lg">

                    Cancel

                </a>

                <button
                    type="submit"
                    class="bg-red-600 hover:bg-red-700 text-white px-8 py-3 rounded-lg font-semibold shadow">

                    Save Activation

                </button>

            </div>

        </form>

    </div>

</div>
{{-- JavaScript --}}
<script>

document.addEventListener('DOMContentLoaded', function () {

    const startDate = document.getElementById('start_date');
    const expiryDate = document.getElementById('expiry_date');

    // Duration Buttons
    document.querySelectorAll('.duration-btn').forEach(button => {

        button.addEventListener('click', function () {

            document.querySelectorAll('.duration-btn').forEach(btn => {

                btn.classList.remove('bg-red-600');
                btn.classList.remove('text-white');
                btn.classList.add('bg-gray-200');

            });

            this.classList.remove('bg-gray-200');
            this.classList.add('bg-red-600');
            this.classList.add('text-white');

            let days = parseInt(this.dataset.days);

            let start = new Date(startDate.value);

            if (isNaN(start.getTime())) {

                start = new Date();

                startDate.valueAsDate = start;

            }

            let expiry = new Date(start);

            expiry.setDate(expiry.getDate() + days);

            let yyyy = expiry.getFullYear();

            let mm = String(expiry.getMonth() + 1).padStart(2,'0');

            let dd = String(expiry.getDate()).padStart(2,'0');

            expiryDate.value = `${yyyy}-${mm}-${dd}`;

        });

    });

    // WhatsApp Counter
    const waMessage = document.getElementById('whatsapp_message');
    const waCount = document.getElementById('waCount');

    function updateWaCount(){

        waCount.innerText = waMessage.value.length;

    }

    if(waMessage){

        updateWaCount();

        waMessage.addEventListener('keyup', updateWaCount);

    }

    // SMS Counter
    const smsMessage = document.getElementById('sms_message');
    const smsCount = document.getElementById('smsCount');

    function updateSmsCount(){

        smsCount.innerText = smsMessage.value.length;

    }

    if(smsMessage){

        updateSmsCount();

        smsMessage.addEventListener('keyup', updateSmsCount);

    }

    // File Preview
    const attachment = document.getElementById('attachment');

    const preview = document.getElementById('filePreview');

    const fileName = document.getElementById('fileName');

    if(attachment){

        attachment.addEventListener('change', function(){

            if(this.files.length){

                preview.classList.remove('hidden');

                fileName.innerHTML = this.files[0].name;

            }else{

                preview.classList.add('hidden');

            }

        });

    }

});
</script>

@endsection