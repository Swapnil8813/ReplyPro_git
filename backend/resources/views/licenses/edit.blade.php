@extends('layouts.app')

@section('title','Generate License')
@section('page-title','Generate License')

@section('content')

<div class="bg-white rounded-xl shadow p-6">

<form action="{{ route('licenses.update', $license) }}" method="POST">
    @csrf
    @method('PUT')

@csrf

<div class="grid md:grid-cols-2 gap-5">

<div>

<label>Client *</label>

<select name="client_id"
class="w-full border rounded-lg p-3">

@foreach($clients as $client)

<option value="{{ $client->id }}">
{{ $client->name }}
</option>

@endforeach

</select>

</div>

<div>

<label>Product *</label>

<input
type="text"
name="product_name"
class="w-full border rounded-lg p-3"
required>

</div>

<div>

<label>Plan</label>

<select
name="plan"
class="w-full border rounded-lg p-3">

<option>Basic</option>
<option>Professional</option>
<option>Enterprise</option>

</select>

</div>

<div>

<label>Device Limit</label>

<input
type="number"
name="device_limit"
value="1"
class="w-full border rounded-lg p-3">

</div>

<div>

<label>Purchase Date</label>

<input
type="date"
name="purchase_date"
class="w-full border rounded-lg p-3"
required>

</div>

<div>

<label>Expiry Date</label>

<input
type="date"
name="expiry_date"
class="w-full border rounded-lg p-3"
required>

</div>

<div class="md:col-span-2">

<label>Remarks</label>

<textarea
name="remarks"
rows="3"
class="w-full border rounded-lg p-3"></textarea>

</div>

</div>

<div class="mt-6">

<button
class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg">

Generate License

</button>

</div>

</form>

</div>

@endsection