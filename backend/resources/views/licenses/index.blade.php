@extends('layouts.app')

@section('title','Licenses')
@section('page-title','Licenses')

@section('content')

<div class="bg-white rounded-xl shadow p-6">

    <div class="flex justify-between items-center mb-6">

        <h2 class="text-2xl font-bold">
            License Management
        </h2>

      <a href="{{ route('licenses.create') }}"
   style="background:#2563eb;color:#fff;padding:12px 20px;border-radius:8px;display:inline-block;">
    + Generate License
</a>

    </div>

    <table class="w-full">

        <thead>

            <tr class="border-b">

                <th class="py-3 text-left">Client</th>
                <th>License Key</th>
                <th>Product</th>
                <th>Plan</th>
                <th>Expiry</th>
                <th>Status</th>
                <th>Devices</th>
                <th>Actions</th>

            </tr>

        </thead>

        <tbody>

        @forelse($licenses as $license)

        <tr class="border-b hover:bg-gray-50">

            <td>{{ $license->client->name }}</td>

            <td class="font-mono text-sm">
                {{ $license->license_key }}
            </td>

            <td>{{ $license->product_name }}</td>

            <td>{{ $license->plan }}</td>

            <td>{{ $license->expiry_date }}</td>

            <td>

                @if($license->status=='Active')

                    <span class="bg-green-100 text-green-700 px-2 py-1 rounded">
                        Active
                    </span>

                @elseif($license->status=='Expired')

                    <span class="bg-red-100 text-red-700 px-2 py-1 rounded">
                        Expired
                    </span>

                @else

                    <span class="bg-yellow-100 text-yellow-700 px-2 py-1 rounded">
                        Suspended
                    </span>

                @endif

            </td>

            <td>
                {{ $license->activated_devices }}/{{ $license->device_limit }}
            </td>

            <td>

                <a href="{{ route('licenses.edit',$license) }}"
                   class="text-blue-600">

                    Edit

                </a>

                |

                <form action="{{ route('licenses.destroy',$license) }}"
                      method="POST"
                      class="inline">

                    @csrf
                    @method('DELETE')

                    <button
                        onclick="return confirm('Delete License?')"
                        class="text-red-600">

                        Delete

                    </button>

                </form>

            </td>

        </tr>

        @empty

        <tr>

            <td colspan="8"
                class="text-center py-8 text-gray-500">

                No Licenses Found.

            </td>

        </tr>

        @endforelse

        </tbody>

    </table>

    <div class="mt-6">

        {{ $licenses->links() }}

    </div>

</div>

@endsection