<x-app-layout>
    <x-slot name="header">
        <div class="flex justify-between items-center">
            <h2 class="font-semibold text-xl text-gray-800 leading-tight">
                Customers
            </h2>

            <a href="{{ route('customers.create') }}"
               class="bg-blue-600 hover:bg-blue-700 text- px-4 py-2 rounded-lg">
                + Add Customer
            </a>
        </div>
    </x-slot>

    <div class="py-6">
        <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">

            <div class="bg-white shadow rounded-lg p-6">

                <h3 class="text-2xl font-bold mb-6">Customer List</h3>

                <p>No customers found.</p>

            </div>

        </div>
    </div>
</x-app-layout>