@extends('layouts.app')

@section('title','Devices')
@section('page-title','Devices')

@section('content')

<div class="bg-white rounded-xl shadow p-6">

    <div class="flex justify-between items-center mb-6">

        <h2 class="text-2xl font-bold">
            Device Management
        </h2>

    </div>

    <table class="w-full">

        <thead>

            <tr class="border-b">

                <th class="py-3 text-left">Client</th>
                <th>License</th>
                <th>Device</th>
                <th>Computer</th>
                <th>OS</th>
                <th>Last Seen</th>
                <th>Status</th>

            </tr>

        </thead>

        <tbody>

        @forelse($devices as $device)

        <tr class="border-b hover:bg-gray-50">

            <td>{{ $device->license->client->name }}</td>

            <td class="font-mono text-sm">
                {{ $device->license->license_key }}
            </td>

            <td>{{ $device->device_name }}</td>

            <td>{{ $device->computer_name }}</td>

            <td>
                {{ $device->os_name }}
                {{ $device->os_version }}
            </td>

            <td>{{ $device->last_seen }}</td>

            <td>

                @if($device->status=='Active')

                    <span class="bg-green-100 text-green-700 px-2 py-1 rounded">
                        Active
                    </span>

                @else

                    <span class="bg-red-100 text-red-700 px-2 py-1 rounded">
                        Blocked
                    </span>

                @endif

            </td>

        </tr>

        @empty

        <tr>

            <td colspan="7" class="text-center py-10 text-gray-500">

                No Devices Registered Yet.

            </td>

        </tr>

        @endforelse

        </tbody>

    </table>

    <div class="mt-6">

        {{ $devices->links() }}

    </div>

</div>

@endsection
